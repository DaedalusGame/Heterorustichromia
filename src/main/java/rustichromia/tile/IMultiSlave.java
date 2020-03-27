package rustichromia.tile;

import rustichromia.block.MultiBlockPart;

public interface IMultiSlave {
    MultiBlockPart getPart();

    void initPart(int x, int y, int z);
}
