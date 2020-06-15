package rustichromia.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.util.vector.Quaternion;
import rustichromia.block.BlockCartControl;
import rustichromia.cart.Control;
import rustichromia.util.FacingToRotation;
import rustichromia.util.Misc;

import javax.annotation.Nullable;

public class TileEntityCartControl extends TileEntity {
    Control control;

    public void setData(Control control) {
        this.control = control;
        markDirty();
    }

    public Control getControl() {
        return control;
    }

    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockCartControl)
            return state.getValue(BlockCartControl.FACING).getOpposite();
        return null;
    }

    public EnumFacing getTrueFacing() {
        return control.getTrueFacing(this.getFacing());
    }

    public Quaternion getRotation(EnumFacing up) {
        if(control == null)
            return new Quaternion();
        EnumFacing forward = getTrueFacing();
        return FacingToRotation.get(forward, up).getQuat();
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if(control != null)
            compound.setTag("control", control.serialize());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        control = Control.deserialize(compound.getCompoundTag("control"));
    }

    public boolean isPowered() {
        return getWorld().isBlockIndirectlyGettingPowered(getPos()) != 0;
    }
}
