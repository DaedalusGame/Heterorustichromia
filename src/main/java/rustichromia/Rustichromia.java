package rustichromia;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustichromia.entity.EntitySpear;
import rustichromia.gui.GuiHandler;
import rustichromia.handler.RestHandler;
import rustichromia.handler.WindHandler;
import rustichromia.handler.PistonHandler;
import rustichromia.network.PacketHandler;
import rustichromia.proxy.IProxy;
import rustichromia.recipe.RecipeRegistry;
import rustichromia.tile.*;
import rustichromia.util.Attributes;
import rustichromia.compat.*;

@Mod(modid = Rustichromia.MODID, acceptedMinecraftVersions = "[1.12, 1.13)", dependencies = "required-after:mysticalmechanics", guiFactory = "rustichromia.gui.GuiFactory")
@Mod.EventBusSubscriber
public class Rustichromia {
    public static final String MODID = "rustichromia";

    @SidedProxy(clientSide = "rustichromia.proxy.ClientProxy", serverSide = "rustichromia.proxy.ServerProxy")
    public static IProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigManager.init(event.getSuggestedConfigurationFile());

        MinecraftForge.EVENT_BUS.register(new Registry());
        MinecraftForge.EVENT_BUS.register(new RecipeRegistry());
        MinecraftForge.EVENT_BUS.register(PistonHandler.class);
        MinecraftForge.EVENT_BUS.register(WindHandler.class);
        MinecraftForge.EVENT_BUS.register(RestHandler.class);
        MinecraftForge.EVENT_BUS.register(Attributes.class);

        if(Loader.isModLoaded(Rustic.MODID))
            Rustic.preInit();
        if(Loader.isModLoaded(MistyWorld.MODID))
            MistyWorld.preInit();

        PacketHandler.registerMessages();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

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
        GameRegistry.registerTileEntity(TileEntityQuern.class, new ResourceLocation(MODID, "quern"));
        GameRegistry.registerTileEntity(TileEntityAssembler.class, new ResourceLocation(MODID, "assembler"));
        GameRegistry.registerTileEntity(TileEntityGin.class, new ResourceLocation(MODID, "gin"));
        GameRegistry.registerTileEntity(TileEntityCrank.class, new ResourceLocation(MODID, "crank"));
        GameRegistry.registerTileEntity(TileEntityWindVane.class, new ResourceLocation(MODID, "windvane"));
        GameRegistry.registerTileEntity(TileEntityHopperWood.class, new ResourceLocation(MODID, "hopper_wood"));
        GameRegistry.registerTileEntity(TileEntityMultiSlave.class, new ResourceLocation(MODID, "multiblock_slave"));
        GameRegistry.registerTileEntity(TileEntityHayCompactor.class, new ResourceLocation(MODID, "hay_compactor"));
        GameRegistry.registerTileEntity(TileEntityHayCompactorInlet.class, new ResourceLocation(MODID, "hay_compactor_inlet"));
        GameRegistry.registerTileEntity(TileEntityFeeder.class, new ResourceLocation(MODID, "feeder"));

        int id = 0;

        EntityRegistry.registerModEntity(new ResourceLocation(Rustichromia.MODID,"spear"), EntitySpear.class, "spear", id++, this, 64, 1, true);

        Registry.init();
        if(Loader.isModLoaded(Rustic.MODID))
            Rustic.init();
        if(Loader.isModLoaded(MistyWorld.MODID))
            MistyWorld.init();

        PROXY.init();
    }
}
