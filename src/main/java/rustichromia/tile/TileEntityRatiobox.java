package rustichromia.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityRatiobox extends TileEntity implements ITickable {
    private EnumFacing sideA, sideB;
    private double ratioOff, ratioOn;
    private boolean active;

    private double getCurrentRatioA() {
        return active ? ratioOn : ratioOff;
    }

    private double getCurrentRatioB() {
        return active ? 1 - ratioOn : 1 - ratioOff;
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            boolean isRedstoneActive = world.isBlockIndirectlyGettingPowered(pos) != 0;
            if (isRedstoneActive != active) {
                active = isRedstoneActive;
            }
            //updateNeighbors
        }
    }
}
