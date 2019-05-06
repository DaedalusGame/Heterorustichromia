package rustichromia;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustichromia.block.*;

import javax.annotation.Nonnull;

import static net.minecraftforge.fml.common.registry.GameRegistry.*;

public class Registry {
    @ObjectHolder("rustichromia:molten_steel")
    public static BlockSnakeFluid MOLTEN_STEEL;
    @ObjectHolder("rustichromia:block_steel")
    public static Block BLOCK_STEEL;
    @ObjectHolder("rustichromia:receptacle")
    public static Block RECEPTACLE;
    @ObjectHolder("rustichromia:extruder")
    public static Block EXTRUDER;
    @ObjectHolder("rustichromia:windmill")
    public static Block WINDMILL;
    @ObjectHolder("rustichromia:windmill_big")
    public static Block WINDMILL_BIG;
    @ObjectHolder("rustichromia:mech_torch")
    public static Block MECH_TORCH;

    @ObjectHolder("rustichromia:windmill_blade")
    public static Item WINDMILL_BLADE;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        MOLTEN_STEEL = (BlockSnakeFluid) new BlockSnakeFluid(MapColor.WHITE_STAINED_HARDENED_CLAY).setRegistryName(Rustichromia.MODID, "molten_steel").setUnlocalizedName("molten_steel").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        BLOCK_STEEL = new Block(Material.IRON,MapColor.WHITE_STAINED_HARDENED_CLAY).setRegistryName(Rustichromia.MODID, "block_steel").setUnlocalizedName("block_steel").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        RECEPTACLE = new BlockMetalReceptacle(Material.IRON).setRegistryName(Rustichromia.MODID, "receptacle").setUnlocalizedName("receptacle").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        EXTRUDER = new BlockExtrusionForm(Material.IRON).setRegistryName(Rustichromia.MODID, "extruder").setUnlocalizedName("extruder").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        WINDMILL = new BlockWindmill(Material.WOOD).setRegistryName(Rustichromia.MODID, "windmill").setUnlocalizedName("windmill").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        WINDMILL_BIG = new BlockWindmill(Material.WOOD) {
            @Override
            public double getScale(World world, BlockPos pos, IBlockState state) {
                return 2.0;
            }
        }.setRegistryName(Rustichromia.MODID, "windmill_big").setUnlocalizedName("windmill_big").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        MECH_TORCH = new BlockMechTorch(Material.WOOD).setRegistryName(Rustichromia.MODID, "mech_torch").setUnlocalizedName("mech_torch").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);

        event.getRegistry().register(MOLTEN_STEEL);
        event.getRegistry().register(BLOCK_STEEL);
        event.getRegistry().register(RECEPTACLE);
        event.getRegistry().register(EXTRUDER);
        event.getRegistry().register(WINDMILL);
        event.getRegistry().register(WINDMILL_BIG);
        event.getRegistry().register(MECH_TORCH);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(MOLTEN_STEEL).setRegistryName(MOLTEN_STEEL.getRegistryName()));
        event.getRegistry().register(new ItemBlock(BLOCK_STEEL).setRegistryName(BLOCK_STEEL.getRegistryName()));
        event.getRegistry().register(new ItemBlock(RECEPTACLE).setRegistryName(RECEPTACLE.getRegistryName()));
        event.getRegistry().register(new ItemBlock(EXTRUDER).setRegistryName(EXTRUDER.getRegistryName()));
        event.getRegistry().register(new ItemBlock(WINDMILL).setRegistryName(WINDMILL.getRegistryName()));
        event.getRegistry().register(new ItemBlock(WINDMILL_BIG).setRegistryName(WINDMILL_BIG.getRegistryName()));
        event.getRegistry().register(new ItemBlock(MECH_TORCH).setRegistryName(MECH_TORCH.getRegistryName()));

        event.getRegistry().register(WINDMILL_BLADE = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"windmill_blade")).setCreativeTab(CreativeTabs.REDSTONE));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registerItemModel(Item.getItemFromBlock(MOLTEN_STEEL), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(BLOCK_STEEL), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(RECEPTACLE), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(EXTRUDER), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(WINDMILL), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(WINDMILL), 1, "blade");
        registerItemModel(Item.getItemFromBlock(WINDMILL_BIG), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(WINDMILL_BIG), 1, "blade");
        registerItemModel(Item.getItemFromBlock(MECH_TORCH), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(MECH_TORCH), 1, "dial");

        registerItemModel(WINDMILL_BLADE, 0, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public void registerItemModel(@Nonnull Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }
}
