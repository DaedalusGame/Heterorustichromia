package rustichromia.tile;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import rustichromia.Registry;
import rustichromia.block.BlockWindmill;
import rustichromia.handler.WindHandler;

import javax.annotation.Nullable;

public class TileEntityWindmill extends TileEntity implements ITickable {
    double lastAngle;
    double angle;
    private double currentPower = 1;
    private int blades = 4;

    public DefaultMechCapability capability = new DefaultMechCapability() {
        @Override
        public double getPower(EnumFacing from) {
            return currentPower;
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            if (from == null) {
                currentPower = value;
                onPowerChange();
            }
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
        public void onPowerChange() {
            updateNeighbors();
            markDirty();
        }
    };

    public int getMaxBlades() {
        IBlockState state = world.getBlockState(pos);
        BlockWindmill block = (BlockWindmill) state.getBlock();
        return block.getMaxBlades(world,pos,state);
    }

    private double getBladeWeight() {
        IBlockState state = world.getBlockState(pos);
        BlockWindmill block = (BlockWindmill) state.getBlock();
        return block.getBladeWeight(world,pos,state);
    }

    public int getBlades() {
        return blades;
    }

    public double getScale() {
        IBlockState state = world.getBlockState(pos);
        BlockWindmill block = (BlockWindmill) state.getBlock();
        return block.getScale(world,pos,state);
    }

    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getValue(BlockWindmill.facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing()) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing()) {
            return MysticalMechanicsAPI.MECH_CAPABILITY.cast(this.capability);
        }
        return super.getCapability(capability, facing);
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldStack = player.getHeldItem(hand);
        if(hand == EnumHand.OFF_HAND)
            return false;
        if(heldStack.getItem() == Registry.WINDMILL_BLADE && blades < getMaxBlades()) {
            blades++;
            heldStack.shrink(1);
            if (heldStack.isEmpty()) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
            markDirty();
            return true;
        }
        return false;
    }

    private static double lerp(double a, double b, double slide) {
        return a*(1-slide)+b*slide;
    }

    private double getBiomeWindPower(double wind) {
        Biome biome = world.getBiome(pos);
        int heightSlide = (pos.getY() - world.getSeaLevel()) / (world.getHeight() - world.getSeaLevel());
        float flatness = MathHelper.clamp(1 - biome.getHeightVariation(),0,1);
        double lower = MathHelper.clampedLerp(flatness*0.5,0.5, heightSlide);
        double upper = MathHelper.clampedLerp(Math.max(0.1,flatness),1, heightSlide);

        if(world.isThundering()) {
            lower *= 2.0;
            upper *= 3.0;
        } else if(world.isRaining()) {
            lower *= 1.5;
            upper *= 2.0;
        }

        return MathHelper.clampedLerp(MathHelper.clamp(lower,0,1),MathHelper.clamp(upper,0,1),wind);
    }

    private void updateBlades() {
        Vec3d windDirection = WindHandler.getWindDirection(world,pos);
        double windPower = 0;
        switch (getFacing().getAxis())
        {
            case X:
                windPower = MathHelper.clamp(Math.abs(windDirection.x),0,1);
                break;
            case Y:
                windPower = MathHelper.clamp(windDirection.lengthVector()/2.0,0,1);
                break;
            case Z:
                windPower = MathHelper.clamp(Math.abs(windDirection.z),0,1);
                break;
        }

        windPower = getBiomeWindPower(windPower);
        double powerEfficiency = 0.8;

        double bladeMod = (Math.pow(powerEfficiency,getBlades()-4)-1)/(powerEfficiency-1);
        double bladeNormal = (Math.pow(powerEfficiency,4)-1)/(powerEfficiency-1);
        bladeMod = 1 + bladeMod / bladeNormal;

        double massSlide = (getBladeWeight() - 1.0) / (8.0 - 1.0);
        double lowPowerMod = lerp(0.0,-2.0, massSlide);
        double highPowerMod = lerp(1.0,2.0, massSlide);

        double truePower = Math.max(0,5.0 * lerp(lowPowerMod,highPowerMod,windPower) * bladeMod);

        capability.setPower(truePower,null);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        capability.setPower(0f, null);
        updateNeighbors();
    }

    public void updateNeighbors() {
        EnumFacing facing = getFacing();
        TileEntity tile = world.getTileEntity(getPos().offset(facing));
        if (tile != null) {
            if (tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite())) {
                if (tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).isInput(facing.getOpposite())) {
                    tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).setPower(capability.getPower(facing.getOpposite()), facing.getOpposite());
                }
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        currentPower = compound.getDouble("power");
        blades = compound.getInteger("blades");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setDouble("power",currentPower);
        compound.setInteger("blades",blades);
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
        if (world.isRemote) {
            lastAngle = angle;
            angle += capability.getPower(null);
        } else {
            updateBlades();
        }
    }
}
