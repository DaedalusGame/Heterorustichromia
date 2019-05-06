package rustichromia.tile;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import rustichromia.block.BlockMechTorch;

import javax.annotation.Nullable;

public class TileEntityMechTorch extends TileEntity  implements ITickable {
    public final double MAX_POWER = 360;
    double power;
    int pulses;
    boolean on = false;

    double lastAngle;
    double angle;

    public DefaultMechCapability capability = new DefaultMechCapability() {
        @Override
        public boolean isOutput(EnumFacing face) {
            return false;
        }

        @Override
        public boolean isInput(EnumFacing face) {
            return true;
        }

        @Override
        public void onPowerChange() {
            markDirty();
        }
    };

    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getValue(BlockMechTorch.facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing().getOpposite()) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing().getOpposite()) {
            return MysticalMechanicsAPI.MECH_CAPABILITY.cast(this.capability);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        capability.readFromNBT(compound);
        angle = compound.getDouble("rotation");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        capability.writeToNBT(compound);
        compound.setDouble("rotation",power);
        return compound;
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
    public void update() {
        double speed = capability.getPower(null);
        if(!world.isRemote) {
            this.power += speed;
            if(this.power >= MAX_POWER) {
                markDirty();
                pulses += this.power / MAX_POWER;
                this.power %= MAX_POWER;
            }

            IBlockState state = world.getBlockState(pos);
            if(on) {
                world.setBlockState(pos,state.withProperty(BlockMechTorch.on,false));
                on = false;
                pulses--;
            } else if(pulses > 0) {
                world.setBlockState(pos,state.withProperty(BlockMechTorch.on,true));
                on = true;
            }
        } else {
            lastAngle = angle;
            angle += speed;
        }
    }
}
