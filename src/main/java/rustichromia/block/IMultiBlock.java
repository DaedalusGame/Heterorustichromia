package rustichromia.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustichromia.tile.IMultiTile;

import javax.annotation.Nullable;

public interface IMultiBlock<T extends TileEntity & IMultiTile> {
    @Nullable
    MultiBlockPart getPart(IBlockAccess world, BlockPos pos);

    default boolean isValid(World world, BlockPos pos) {
        MultiBlockPart part = getPart(world, pos);
        if (part == null)
            return false;
        T master = getMaster(world,pos);
        if(master == null)
            return false;
        return true;
    }

    @Nullable
    default T getMaster(World world, BlockPos pos) {
        MultiBlockPart part = getPart(world, pos);
        if(part != null)
            return getMaster(world,pos,part);
        return null;
    }

    @Nullable
    default T getMaster(World world, BlockPos pos, MultiBlockPart part) {
        TileEntity master = world.getTileEntity(pos.add(part.getMasterOffset()));
        if(master instanceof IMultiTile)
            return (T)master;
        return null;
    }

    default boolean isMaster(World world, BlockPos pos) {
        MultiBlockPart part = getPart(world, pos);
        if (part == null)
            return false;
        return part.getSlaveOffset().equals(Vec3i.NULL_VECTOR);
    }

    void breakPart(World world, BlockPos pos);

    default void checkValidMultiblock(World world, BlockPos pos) {
        T master = getMaster(world,pos);
        if(master == null) {
            breakPart(world, pos);
            return;
        }
        if(!master.isMultiBlockValid())
            return;
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos neighborPos = pos.offset(facing);
            if(!master.isPartValid(neighborPos)) {
                master.destroy(neighborPos);
                return;
            }
        }
    }
}
