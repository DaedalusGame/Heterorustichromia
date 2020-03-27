package rustichromia.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;

public class MultiBlockPart implements INBTSerializable<NBTTagCompound> {
    Vec3i toSlave;
    Vec3i toMaster;

    public MultiBlockPart(int x, int y, int z) {
        toSlave = new Vec3i(x,y,z);
        toMaster = new Vec3i(-x,-y,-z);
    }

    public MultiBlockPart(NBTTagCompound nbt)
    {
        deserializeNBT(nbt);
    }

    public Vec3i getSlaveOffset() {
        return toSlave;
    }

    public Vec3i getMasterOffset() {
        return toMaster;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x",toSlave.getX());
        nbt.setInteger("y",toSlave.getY());
        nbt.setInteger("z",toSlave.getZ());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");
        toSlave = new Vec3i(x,y,z);
        toMaster = new Vec3i(-x,-y,-z);
    }
}
