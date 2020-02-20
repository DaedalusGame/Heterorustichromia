package rustichromia.tile;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import rustichromia.block.BlockCrank;

import javax.annotation.Nullable;

public class TileEntityCrank extends TileEntity implements ITickable {
    double lastAngle;
    double angle;
    int windup;
    double windupPower;

    public IMechCapability mechPower;

    public TileEntityCrank() {
        mechPower = new DefaultMechCapability() {
            @Override
            public void setPower(double value, EnumFacing from) {
                if (from == null)
                    super.setPower(value, null);
            }

            @Override
            public boolean isOutput(EnumFacing face) {
                return true;
            }
            @Override
            public boolean isInput(EnumFacing face) {
                return false;
            }

            @Override
            public void onPowerChange(){
                updateNeighbors();
                markDirty();
            }
        };
    }

    public EnumFacing getFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        return state.getValue(BlockCrank.facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing().getOpposite()){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing().getOpposite()){
            return (T) this.mechPower;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        double power = MathHelper.clampedLerp(0,windupPower,windup / 100.0);
        if (world.isRemote) {
            lastAngle = angle;
            angle += power;
        } else {
            windup--;
            mechPower.setPower(power, null);
            updateNeighbors();
            markDirty();
        }
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        mechPower.setPower(0f, null);
        updateNeighbors();
    }

    public void updateNeighbors() {
        EnumFacing facing = getFacing().getOpposite();
        TileEntity tile = world.getTileEntity(getPos().offset(facing));
        if (tile != null) {
            if (tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite())) {
                if (tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).isInput(facing.getOpposite())) {
                    tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).setPower(mechPower.getPower(facing.getOpposite()), facing.getOpposite());
                }
            }
        }
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        windup = 100;
        windupPower = 5;
        return true;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this, false);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        mechPower.readFromNBT(compound);
        windup = compound.getInteger("windup");
        windupPower = compound.getDouble("windup_power");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        mechPower.writeToNBT(compound);
        compound.setInteger("windup", windup);
        compound.setDouble("windup_power", windupPower);
        return compound;
    }
}
