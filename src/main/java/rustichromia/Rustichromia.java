package rustichromia;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustichromia.entity.EntitySpear;
import rustichromia.handler.WindHandler;
import rustichromia.handler.PistonHandler;
import rustichromia.network.PacketHandler;
import rustichromia.proxy.IProxy;
import rustichromia.recipe.RecipeRegistry;
import rustichromia.tile.*;
import rustichromia.util.Attributes;

@Mod(modid = Rustichromia.MODID, acceptedMinecraftVersions = "[1.12, 1.13)")
@Mod.EventBusSubscriber
public class Rustichromia {
    public static final String MODID = "rustichromia";

    @SidedProxy(clientSide = "rustichromia.proxy.ClientProxy", serverSide = "rustichromia.proxy.ServerProxy")
    public static IProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new Registry());
        MinecraftForge.EVENT_BUS.register(new RecipeRegistry());
        MinecraftForge.EVENT_BUS.register(PistonHandler.class);
        MinecraftForge.EVENT_BUS.register(WindHandler.class);
        MinecraftForge.EVENT_BUS.register(Attributes.class);

        PacketHandler.registerMessages();

        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityExtrusionForm.class, new ResourceLocation(MODID, "extruder"));
        GameRegistry.registerTileEntity(TileEntityWindmill.class, new ResourceLocation(MODID, "windmill"));
        GameRegistry.registerTileEntity(TileEntityMechTorch.class, new ResourceLocation(MODID, "mech_torch"));
        GameRegistry.registerTileEntity(TileEntityMechTorchToggle.class, new ResourceLocation(MODID, "mech_torch_toggle"));
        GameRegistry.registerTileEntity(TileEntityAxleWood.class, new ResourceLocation(MODID, "axle_wood"));
        GameRegistry.registerTileEntity(TileEntityRatiobox.class, new ResourceLocation(MODID, "ratiobox"));
        GameRegistry.registerTileEntity(TileEntityPress.class, new ResourceLocation(MODID, "press"));

        int id = 0;

        EntityRegistry.registerModEntity(new ResourceLocation(Rustichromia.MODID,"spear"), EntitySpear.class, "spear", id++, this, 64, 1, true);

        Registry.init();

        PROXY.init();
    }
}
