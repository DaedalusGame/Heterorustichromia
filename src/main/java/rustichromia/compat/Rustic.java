package rustichromia.compat;

import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import rustichromia.Registry;
import rustichromia.Rustichromia;
import rustichromia.block.BlockPress;
import rustichromia.tile.TileEntityPress;

public class Rustic {
    public static final String MODID = "rustic";

    @GameRegistry.ObjectHolder("rustichromia:press")
    public static Block PRESS;

    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(Rustic.class);
    }

    public static void init() {
        GameRegistry.registerTileEntity(TileEntityPress.class, new ResourceLocation(Rustichromia.MODID, "press"));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        CreativeTabs mystmechTab = MysticalMechanicsAPI.IMPL.getCreativeTab();

        PRESS = new BlockPress(Material.WOOD).setRegistryName(Rustichromia.MODID, "press").setUnlocalizedName("press").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);

        event.getRegistry().register(PRESS);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(PRESS).setRegistryName(PRESS.getRegistryName()));
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new ShapedOreRecipe(getRL("press"),new ItemStack(PRESS,1),true,new Object[]{
                "WWW", "IGI", "WPW",
                'W', "plankWood",
                'P', new ItemStack(Blocks.PISTON),
                'I', new ItemStack(Registry.AXLE_WOOD),
                'G', "gearWood"}).setRegistryName(getRL("press")));
    }

    private static ResourceLocation getRL(String name) {
        return new ResourceLocation(Rustichromia.MODID,name);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Registry.registerItemModel(Item.getItemFromBlock(PRESS), 0, "inventory");
        Registry.registerItemModel(Item.getItemFromBlock(PRESS), 1, "extension");
        Registry.registerItemModel(Item.getItemFromBlock(PRESS), 2, "head");
    }
}
