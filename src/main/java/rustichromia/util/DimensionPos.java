package rustichromia.util;

import net.minecraft.util.math.BlockPos;

public class DimensionPos {
    private BlockPos pos;
    private int dimension;

    public BlockPos getPos() {
        return pos;
    }

    public int getDimension() {
        return dimension;
    }

    public DimensionPos(BlockPos pos, int dimension) {
        this.pos = pos;
        this.dimension = dimension;
    }

    //hashcode stolen from Botania
    @Override
    public int hashCode() {
        return 31 * dimension ^ pos.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof DimensionPos)
            return equals((DimensionPos) other);
        return false;
    }

    private boolean equals(DimensionPos other) {
        return pos.equals(other.pos) && dimension == other.dimension;
    }
}
