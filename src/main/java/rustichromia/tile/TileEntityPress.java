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
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import rustic.common.tileentity.TileEntityCrushingTub;
import rustichromia.block.BlockMechTorch;
import rustichromia.block.BlockPress;

import javax.annotation.Nullable;

public class TileEntityPress extends TileEntity implements ITickable {
    protected boolean isBroken;

    public double lastExtend, extend;
    private boolean down = true;

    public PressCapability capability;

    public TileEntityPress() {
        super();
        capability = new PressCapability();
    }

    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getValue(BlockPress.facing);
    }

    public boolean isForward(EnumFacing facing) {
        if(facing == null)
            return false;
        return facing == getFacing();
    }

    public boolean isBackward(EnumFacing facing) {
        if(facing == null)
            return false;
        return facing == getFacing().getOpposite();
    }

    public boolean isValidSide(EnumFacing facing) {
        return isForward(facing) || isBackward(facing);
    }

    @Override
    public void update() {
        double speed = capability.forwardPower - capability.backwardPower;

        if(down)
            speed *= 0.01;
        else
            speed *= 0.01;
        if(speed > 0 && !down)
            speed = 0;
        else if(speed < 0 && down)
            speed = 0;

        if(extend >= 1 && down) {
            down = false;
            squish();
        }
        else if(extend <= 0 && !down)
            down = true;

        lastExtend = extend;
        extend = MathHelper.clamp(extend+speed,0,1);
    }

    private void squish() {
        TileEntity tile = world.getTileEntity(pos.down());
        if(tile instanceof TileEntityCrushingTub) {
            ((TileEntityCrushingTub) tile).crush(null);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        capability.readFromNBT(compound);
        extend = compound.getDouble("extend");
        down = compound.getBoolean("down");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        capability.writeToNBT(compound);
        compound.setDouble("extend",extend);
        compound.setBoolean("down",down);
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
        Misc.syncTE(this, isBroken);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (this.capability.isInput(facing) || this.capability.isOutput(facing))) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (this.capability.isInput(facing) || this.capability.isOutput(facing))) {
            return MysticalMechanicsAPI.MECH_CAPABILITY.cast(this.capability);
        }
        return super.getCapability(capability, facing);
    }

    private class PressCapability extends DefaultMechCapability {
        double forwardPower;
        double backwardPower;

        @Override
        public void onPowerChange(){
            markDirty();
        }

        @Override
        public double getPower(EnumFacing from) {
            if(from == null || isValidSide(from)) {
                //this should only really be called on block break.
                return getActualPower();
            }
            return 0;
        }

        private double getActualPower() {
            return Math.abs(forwardPower - backwardPower);
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            if(from == null) {
                forwardPower = 0;
                backwardPower = 0;
                onPowerChange();
            }else if(isForward(from)){
                double oldPower = backwardPower;
                if (oldPower != value){
                    backwardPower = value;
                    onPowerChange();
                }
            }else if(isBackward(from)){
                double oldPower = forwardPower;
                if (oldPower != value){
                    forwardPower = value;
                    onPowerChange();
                }
            }
        }

        @Override
        public boolean isInput(EnumFacing from) {
            return isValidSide(from);
        }

        @Override
        public boolean isOutput(EnumFacing from) {
            return false;
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setDouble("forwardPower",forwardPower);
            tag.setDouble("backwardPower",backwardPower);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            forwardPower = tag.getDouble("forwardPower");
            backwardPower = tag.getDouble("backwardPower");
        }
    }
}
