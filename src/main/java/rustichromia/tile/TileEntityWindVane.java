package rustichromia.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import rustichromia.handler.WindHandler;

public class TileEntityWindVane extends TileEntity implements ITickable {
    double lastAngle;
    double angle;

    @Override
    public void update() {
        Vec3d windDirection = WindHandler.getWindDirection(getWorld(),getPos());
        double windAngle = Math.atan2(windDirection.z, windDirection.x);

        lastAngle = angle;
        angle += shortAngleDist(angle,Math.toDegrees(windAngle)) * 0.6;
    }

    private double shortAngleDist(double a0, double a1) {
        double max = 360;
        double da = (a1 - a0) % max;
        return 2*da % max - da;
    }

    private double angleLerp(double a0,double a1, double t) {
        return a0 + shortAngleDist(a0,a1)*t;
    }
}
