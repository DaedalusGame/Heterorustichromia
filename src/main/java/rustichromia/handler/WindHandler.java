package rustichromia.handler;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rustichromia.util.OpenSimplexNoise;

import java.util.HashMap;

public class WindHandler {
    private static HashMap<Integer,WindHandler> handlers = new HashMap<>();

    private OpenSimplexNoise noiseMap;
    private World world;

    public static WindHandler get(World world) {
        return handlers.get(getDimension(world));
    }

    public static Vec3d getWindDirection(World world, BlockPos pos) {
        WindHandler handler = get(world);
        if(handler != null)
            return handler.getWindDirection(pos);
        else
            return Vec3d.ZERO;
    }

    private static int getDimension(World world){
        return world.provider.getDimension();
    }

    private WindHandler(World world) {
        this.world = world;
        this.noiseMap = new OpenSimplexNoise(getSeed());
    }

    private int getDimension() {
        return getDimension(world);
    }

    private long getTime() {
        return world.getTotalWorldTime();
    }

    private long getSeed() {
        return world.getSeed();
    }

    public Vec3d getWindDirection(BlockPos pos) {
        double unit = 1.0 / 64;
        double timeUnit = 1.0 / 1200;
        double centerx = pos.getX() * unit;
        double centery = pos.getY() * unit;
        double centerz = getTime() * timeUnit;

        double x = 1*noiseMap.eval(centerx+unit,centery+unit,centerz) +
                -1*noiseMap.eval(centerx-unit,centery+unit,centerz) +
                -1*noiseMap.eval(centerx-unit,centery-unit,centerz) +
                1*noiseMap.eval(centerx+unit,centery-unit,centerz) +
                2*noiseMap.eval(centerx+unit,centery,centerz) +
                -2*noiseMap.eval(centerx-unit,centery,centerz);
        double y = 1*noiseMap.eval(centerx+unit,centery+unit,centerz) +
                1*noiseMap.eval(centerx-unit,centery+unit,centerz) +
                -1*noiseMap.eval(centerx-unit,centery-unit,centerz) +
                -1*noiseMap.eval(centerx+unit,centery-unit,centerz) +
                2*noiseMap.eval(centerx,centery+unit,centerz) +
                -2*noiseMap.eval(centerx,centery-unit,centerz);

        return new Vec3d(x,0,y);
    }

    @SubscribeEvent
    public static void onLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if(!world.isRemote && !handlers.containsKey(getDimension(world))) {
            WindHandler handler = new WindHandler(world);
            handlers.put(handler.getDimension(),handler);
        }
    }
}
