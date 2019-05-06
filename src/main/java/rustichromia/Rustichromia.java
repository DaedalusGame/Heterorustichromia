package rustichromia;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustichromia.handler.PistonHandler;
import rustichromia.proxy.IProxy;
import rustichromia.tile.TileEntityExtrusionForm;
import rustichromia.tile.TileEntityMechTorch;
import rustichromia.tile.TileEntityWindmill;

@Mod(modid = Rustichromia.MODID, acceptedMinecraftVersions = "[1.12, 1.13)")
@Mod.EventBusSubscriber
public class Rustichromia {
    public static final String MODID = "rustichromia";

    @SidedProxy(clientSide = "rustichromia.proxy.ClientProxy", serverSide = "rustichromia.proxy.ServerProxy")
    public static IProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Registry());
        MinecraftForge.EVENT_BUS.register(PistonHandler.class);

        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityExtrusionForm.class, new ResourceLocation(MODID, "extruder"));
        GameRegistry.registerTileEntity(TileEntityWindmill.class, new ResourceLocation(MODID, "windmill"));
        GameRegistry.registerTileEntity(TileEntityMechTorch.class, new ResourceLocation(MODID, "mech_torch"));
    }
}
