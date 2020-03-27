package rustichromia.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import rustichromia.block.MultiBlockPart;
import rustichromia.util.Misc;

import javax.annotation.Nullable;

public class TileEntityMultiSlave extends TileEntity implements IMultiSlave {
    MultiBlockPart part;

    public void initPart(int x, int y, int z) {
        part = new MultiBlockPart(x,y,z);
        markDirty();
    }

    public MultiBlockPart getPart() {
        return part;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        deserializeNBT(tag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setTag("multiBlockOffset", part.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        part = new MultiBlockPart(compound.getCompoundTag("multiBlockOffset"));
        super.readFromNBT(compound);
    }
}
