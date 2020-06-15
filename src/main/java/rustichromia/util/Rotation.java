package rustichromia.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Quaternion;

public class Rotation {
    public final float x, y, z;

    public Rotation(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Rotation alternate() {
        return new Rotation(x + 180, -y,z + 180);
    }

    public Rotation getShortestRotation(Rotation other) {
        Rotation alt = alternate();
        if(getRotationDistance(other) < alt.getRotationDistance(other))
            return this;
        else
            return alt;
    }

    public float getRotationDistance(Rotation other) {
        return Math.abs(Misc.angleDistance(x,other.x)) + Math.abs(Misc.angleDistance(y,other.y)) + Math.abs(Misc.angleDistance(z,other.z));
    }

    public static Rotation lerp(Rotation a, Rotation b, float slide) {
        /*Quaternion quatA = a.toQuaternion();
        Quaternion quatB = b.toQuaternion();
        return fromQuaternion(Misc.slerp(quatA,quatB,slide));*/

        Rotation rotation = new Rotation(
                Misc.lerpAngle(a.x, b.x, slide),
                Misc.lerpAngle(a.y, b.y, slide),
                Misc.lerpAngle(a.z, b.z, slide)
        );
        return new Rotation(rotation.x + 180, -rotation.y,rotation.z + 180);
    }

    public void GLrotate() {
        GlStateManager.rotate(x, 1, 0, 0);
        GlStateManager.rotate(y, 0, 1, 0);
        GlStateManager.rotate(z, 0, 0, 1);
    }

    public Quaternion toQuaternion() {
        float f = (float) Math.toRadians(x);
        float f1 = (float) Math.toRadians(y);
        float f2 = (float) Math.toRadians(z);

        float f3 = MathHelper.sin(0.5F * f);
        float f4 = MathHelper.cos(0.5F * f);
        float f5 = MathHelper.sin(0.5F * f1);
        float f6 = MathHelper.cos(0.5F * f1);
        float f7 = MathHelper.sin(0.5F * f2);
        float f8 = MathHelper.cos(0.5F * f2);
        return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7, f4 * f6 * f8 - f3 * f5 * f7);

    }

    /*public static Rotation fromQuaternion(Quaternion quat) {
        float roll, pitch, yaw;

        double sinr_cosp = 2 * (quat.w * quat.x + quat.y * quat.z);
        double cosr_cosp = 1 - 2 * (quat.x * quat.x + quat.y * quat.y);
        roll = (float) Math.atan2(cosr_cosp, sinr_cosp);

        // pitch (y-axis rotation)
        double sinp = 2 * (quat.w * quat.y - quat.z * quat.x);
        if (Math.abs(sinp) >= 1)
            pitch = (float) copysign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            pitch = (float) Math.asin(sinp);

        // yaw (z-axis rotation)
        double siny_cosp = 2 * (quat.w * quat.z + quat.x * quat.y);
        double cosy_cosp = 1 - 2 * (quat.y * quat.y + quat.z * quat.z);
        yaw = (float) Math.atan2(cosy_cosp, siny_cosp);

        return new Rotation(
                (float)Math.toDegrees(yaw),
                (float)Math.toDegrees(roll),
                (float)Math.toDegrees(pitch)
        );
    }*/

    public static Rotation fromQuaternion(Quaternion quat) {
        float x = quat.x;
        float y = quat.y;
        float z = quat.z;
        float w = quat.w;

        float roll = (float) Math.atan2(2 * y * w + 2 * x * z, 1 - 2 * y * y - 2 * z * z);
        float pitch = (float) Math.atan2(2 * x * w + 2 * y * z, 1 - 2 * x * x - 2 * z * z);
        float yaw = (float) Math.asin(2 * x * y + 2 * z * w);

        return new Rotation(
                (float)Math.toDegrees(yaw),
                (float)Math.toDegrees(roll),
                (float)Math.toDegrees(pitch)
        );
    }

    private static double copysign(double x, double y) {
        return Math.abs(x) * Math.signum(y);
    }
}
