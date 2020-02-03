package rustichromia.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import rustichromia.entity.EntitySpear;
import rustichromia.entity.LayerSpear;
import rustichromia.entity.RenderSpear;
import rustichromia.particle.ParticleRenderer;
import rustichromia.particle.ParticleSmoke;
import rustichromia.tile.*;

import java.awt.*;

public class ClientProxy implements IProxy {
    ParticleRenderer renderer;

    @Override
    public void preInit() {
        renderer = new ParticleRenderer();

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmill.class, new TileEntityWindmillRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMechTorch.class, new TileEntityMechTorchRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRatiobox.class, new TileEntityRatioboxRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPress.class, new TileEntityPressRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntitySpear.class, new RenderSpear.Factory());
    }

    @Override
    public void init() {
        LayerSpear.initialize();
    }

    @Override
    public void emitSmoke(World world, double x, double y, double z, double vx, double vy, double vz, Color color, float scaleMin, float scaleMax, int lifetime, float partialTime) {
        renderer.addParticle(new ParticleSmoke(world, x, y, z, vx, vy, vz, color, scaleMin, scaleMax, lifetime, partialTime));
    }

    @Override
    public boolean isThirdPerson() {
        return Minecraft.getMinecraft().gameSettings.thirdPersonView != 0;
    }
}
