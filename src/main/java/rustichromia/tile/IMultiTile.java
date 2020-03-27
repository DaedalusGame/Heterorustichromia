package rustichromia.tile;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IMultiTile extends IMultiSlave {
    void build();

    void destroy(@Nullable BlockPos leak);

    boolean isMultiBlockValid();

    boolean isPartValid(BlockPos pos);
}
