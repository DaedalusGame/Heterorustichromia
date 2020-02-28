package rustichromia.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rustichromia.handler.WindHandler;

public class TileEntityWindVane extends TileEntity implements ITickable {
    double lastAngle;
    double angle;

    int windup;
    double windupPower;

    @Override
    public void update() {
        Vec3d windDirection = WindHandler.getWindDirection(getWorld(),getPos());
        double windAngle = Math.atan2(windDirection.z, windDirection.x);

        lastAngle = angle;

        double correction = MathHelper.clamp(shortAngleDist(angle, Math.toDegrees(windAngle)) * 0.3, -10, 10);
        angle += MathHelper.clampedLerp(correction,windupPower,windup / 100.0);
        windup--;
    }

    public void rotateTile(World world, BlockPos pos, EnumFacing side) {
        windup = 100;
        windupPower = 15;
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
