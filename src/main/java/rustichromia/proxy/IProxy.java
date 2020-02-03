package rustichromia.proxy;

import net.minecraft.world.World;

import java.awt.*;

public interface IProxy {
    void preInit();

    void init();

    void emitSmoke(World world, double x, double y, double z, double vx, double vy, double vz, Color color, float scaleMin, float scaleMax, int lifetime, float partialTime);

    boolean isThirdPerson();
}
