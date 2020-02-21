package rustichromia;

import mysticalmechanics.api.IGearBehavior;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import rustichromia.block.*;
import rustichromia.item.ItemBlastSpear;
import rustichromia.item.ItemCottonCandy;
import rustichromia.item.ItemDisk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    @ObjectHolder("rustichromia:mech_torch_toggle")
    public static Block MECH_TORCH_TOGGLE;
    @ObjectHolder("rustichromia:axle_wood")
    public static Block AXLE_WOOD;
    @ObjectHolder("rustichromia:ratiobox")
    public static Block RATIOBOX;
    @ObjectHolder("rustichromia:quern")
    public static Block QUERN;
    @ObjectHolder("rustichromia:gin")
    public static Block GIN;
    @ObjectHolder("rustichromia:crank")
    public static Block CRANK;
    @ObjectHolder("rustichromia:assembler1")
    public static Block ASSEMBLER_1;
    @ObjectHolder("rustichromia:assembler2")
    public static Block ASSEMBLER_2;
    @ObjectHolder("rustichromia:assembler3")
    public static Block ASSEMBLER_3;
    @ObjectHolder("rustichromia:cotton_seed")
    public static Block COTTON_SEED;

    @ObjectHolder("rustichromia:windmill_blade")
    public static Item WINDMILL_BLADE;
    @ObjectHolder("rustichromia:gear_speckled")
    public static Item GEAR_SPECKLED;
    @ObjectHolder("rustichromia:gear_wood")
    public static Item GEAR_WOOD;
    @ObjectHolder("rustichromia:disk_stone")
    public static Item DISK_STONE;
    @ObjectHolder("rustichromia:disk_sandstone")
    public static Item DISK_SANDSTONE;
    @ObjectHolder("rustichromia:disk_red_sandstone")
    public static Item DISK_RED_SANDSTONE;
    @ObjectHolder("rustichromia:spear")
    public static Item SPEAR;
    @ObjectHolder("rustichromia:blastspear")
    public static Item BLASTSPEAR;
    @ObjectHolder("rustichromia:shamshir")
    public static Item SHAMSHIR;
    @ObjectHolder("rustichromia:cotton_candy")
    public static Item COTTON_CANDY;
    @ObjectHolder("rustichromia:cotton_candy_stick")
    public static Item COTTON_CANDY_STICK;
    @ObjectHolder("rustichromia:cotton")
    public static Item COTTON;
    @ObjectHolder("rustichromia:cotton_wool")
    public static Item COTTON_WOOL;
    @ObjectHolder("rustichromia:dust_wood")
    public static Item DUST_WOOD;
    @ObjectHolder("rustichromia:plate_wood")
    public static Item PLATE_WOOD;
    @ObjectHolder("rustichromia:dust_flour")
    public static Item DUST_FLOUR;

    public static void init() {
        //This doesn't work right.
        /*MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(Rustichromia.MODID, "gear_speckled"), Ingredient.fromItem(GEAR_SPECKLED), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing side, ItemStack gear, double power) {
                if(tile != null && tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY,side)) {
                    IMechCapability capability = tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY,side);
                    double actualPower = 0;
                    for (EnumFacing facing : EnumFacing.VALUES) {
                        if(side != facing && capability.isInput(facing)) {
                            double testPower = capability.getPower(facing);
                            if(testPower > 0 && actualPower < testPower)
                                actualPower = testPower;
                        }
                    }
                    return Math.min(actualPower,power);
                }
                return 0;
            }

            @Override
            public void visualUpdate(TileEntity tile, @Nullable EnumFacing side, ItemStack gear) {
                //NOOP
            }
        });*/
        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(Rustichromia.MODID, "disk_stone"), Ingredient.fromItem(DISK_STONE), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing side, ItemStack gear, double power) {
                ItemDisk item = (ItemDisk) gear.getItem();
                double threshold = item.getAmount(gear);
                if(power < threshold)
                    return 0;
                else
                    return power;
            }

            @Override
            public void visualUpdate(TileEntity tileEntity, @Nullable EnumFacing enumFacing, ItemStack itemStack) {
                //NOOP
            }
        });
        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(Rustichromia.MODID, "disk_sandstone"), Ingredient.fromItem(DISK_SANDSTONE), new IGearBehavior() {
            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing side, ItemStack gear, double power) {
                ItemDisk item = (ItemDisk) gear.getItem();
                double threshold = item.getAmount(gear);
                if(power >= threshold)
                    return threshold;
                else
                    return power;
            }

            @Override
            public void visualUpdate(TileEntity tileEntity, @Nullable EnumFacing enumFacing, ItemStack itemStack) {
                //NOOP
            }
        });
        MysticalMechanicsAPI.IMPL.registerGear(new ResourceLocation(Rustichromia.MODID, "disk_red_sandstone"), Ingredient.fromItem(DISK_RED_SANDSTONE), new IGearBehavior() {
            double epsilion = 1.0E-5D;

            @Override
            public double transformPower(TileEntity tile, @Nullable EnumFacing side, ItemStack gear, double power) {
                ItemDisk item = (ItemDisk) gear.getItem();
                double threshold = item.getAmount(gear);
                if(power >= threshold && power <= threshold + epsilion)
                    return threshold;
                else
                    return 0;
            }

            @Override
            public void visualUpdate(TileEntity tileEntity, @Nullable EnumFacing enumFacing, ItemStack itemStack) {
                //NOOP
            }
        });
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        CreativeTabs mystmechTab = MysticalMechanicsAPI.IMPL.getCreativeTab();

        MOLTEN_STEEL = (BlockSnakeFluid) new BlockSnakeFluid(MapColor.WHITE_STAINED_HARDENED_CLAY).setRegistryName(Rustichromia.MODID, "molten_steel").setUnlocalizedName("molten_steel").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        BLOCK_STEEL = new Block(Material.IRON,MapColor.WHITE_STAINED_HARDENED_CLAY).setRegistryName(Rustichromia.MODID, "block_steel").setUnlocalizedName("block_steel").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        RECEPTACLE = new BlockMetalReceptacle(Material.IRON).setRegistryName(Rustichromia.MODID, "receptacle").setUnlocalizedName("receptacle").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        EXTRUDER = new BlockExtrusionForm(Material.IRON).setRegistryName(Rustichromia.MODID, "extruder").setUnlocalizedName("extruder").setCreativeTab(CreativeTabs.REDSTONE).setHardness(5.0F).setResistance(10.0F);
        WINDMILL = new BlockWindmill(Material.WOOD) {
            @Override
            public double getScale(World world, BlockPos pos, IBlockState state) {
                return 1.0;
            }

            @Override
            public int getMaxBlades(World world, BlockPos pos, IBlockState state) {
                return ConfigManager.windmillBlades;
            }

            @Override
            public double getBladeWeight(World world, BlockPos pos, IBlockState state) {
                return 1.0;
            }
        }.setRegistryName(Rustichromia.MODID, "windmill").setUnlocalizedName("windmill").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        WINDMILL_BIG = new BlockWindmill(Material.WOOD) {
            @Override
            public double getScale(World world, BlockPos pos, IBlockState state) {
                return 2.0;
            }

            @Override
            public int getMaxBlades(World world, BlockPos pos, IBlockState state) {
                return ConfigManager.windmillBigBlades;
            }

            @Override
            public double getBladeWeight(World world, BlockPos pos, IBlockState state) {
                return 8.0;
            }
        }.setRegistryName(Rustichromia.MODID, "windmill_big").setUnlocalizedName("windmill_big").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        MECH_TORCH = new BlockMechTorch(Material.WOOD).setRegistryName(Rustichromia.MODID, "mech_torch").setUnlocalizedName("mech_torch").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        MECH_TORCH_TOGGLE = new BlockMechTorchToggle(Material.WOOD).setRegistryName(Rustichromia.MODID, "mech_torch_toggle").setUnlocalizedName("mech_torch_toggle").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        AXLE_WOOD = new BlockAxleWood(Material.WOOD).setRegistryName(Rustichromia.MODID, "axle_wood").setUnlocalizedName("axle_wood").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        RATIOBOX = new BlockRatiobox(Material.WOOD).setRegistryName(Rustichromia.MODID, "ratiobox").setUnlocalizedName("ratiobox").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        QUERN = new BlockQuern(Material.ROCK).setRegistryName(Rustichromia.MODID, "quern").setUnlocalizedName("quern").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        GIN = new BlockGin(Material.WOOD).setRegistryName(Rustichromia.MODID, "gin").setUnlocalizedName("gin").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        CRANK = new BlockCrank(Material.WOOD).setRegistryName(Rustichromia.MODID, "crank").setUnlocalizedName("crank").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        ASSEMBLER_1 = new BlockAssembler(Material.WOOD, 2) {
            @Override
            public ItemStack getDisplayGear() {
                return new ItemStack(GEAR_WOOD);
            }

            @Override
            public int getTier() {
                return 1;
            }
        }.setRegistryName(Rustichromia.MODID, "assembler1").setUnlocalizedName("assembler1").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        ASSEMBLER_2 = new BlockAssembler(Material.IRON, 4){
            @Override
            public ItemStack getDisplayGear() {
                return new ItemStack(RegistryHandler.IRON_GEAR);
            }

            @Override
            public int getTier() {
                return 2;
            }
        }.setRegistryName(Rustichromia.MODID, "assembler2").setUnlocalizedName("assembler2").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        ASSEMBLER_3 = new BlockAssembler(Material.IRON, 6){
            @Override
            public ItemStack getDisplayGear() {
                return new ItemStack(RegistryHandler.GOLD_GEAR);
            }

            @Override
            public int getTier() {
                return 3;
            }
        }.setRegistryName(Rustichromia.MODID, "assembler3").setUnlocalizedName("assembler3").setCreativeTab(mystmechTab).setHardness(5.0F).setResistance(10.0F);
        COTTON_SEED = new Block(Material.PLANTS).setRegistryName(Rustichromia.MODID, "cotton_seed").setUnlocalizedName("cotton_seed").setCreativeTab(CreativeTabs.MATERIALS).setHardness(5.0F).setResistance(10.0F);

        event.getRegistry().register(MOLTEN_STEEL);
        event.getRegistry().register(BLOCK_STEEL);
        event.getRegistry().register(RECEPTACLE);
        event.getRegistry().register(EXTRUDER);
        event.getRegistry().register(WINDMILL);
        event.getRegistry().register(WINDMILL_BIG);
        event.getRegistry().register(MECH_TORCH);
        event.getRegistry().register(MECH_TORCH_TOGGLE);
        event.getRegistry().register(AXLE_WOOD);
        event.getRegistry().register(RATIOBOX);
        event.getRegistry().register(QUERN);
        event.getRegistry().register(GIN);
        event.getRegistry().register(CRANK);
        event.getRegistry().register(ASSEMBLER_1);
        event.getRegistry().register(ASSEMBLER_2);
        event.getRegistry().register(ASSEMBLER_3);
        event.getRegistry().register(COTTON_SEED);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        CreativeTabs mystmechTab = MysticalMechanicsAPI.IMPL.getCreativeTab();

        event.getRegistry().register(new ItemBlock(MOLTEN_STEEL).setRegistryName(MOLTEN_STEEL.getRegistryName()));
        event.getRegistry().register(new ItemBlock(BLOCK_STEEL).setRegistryName(BLOCK_STEEL.getRegistryName()));
        event.getRegistry().register(new ItemBlock(RECEPTACLE).setRegistryName(RECEPTACLE.getRegistryName()));
        event.getRegistry().register(new ItemBlock(EXTRUDER).setRegistryName(EXTRUDER.getRegistryName()));
        event.getRegistry().register(new ItemBlock(WINDMILL).setRegistryName(WINDMILL.getRegistryName()));
        event.getRegistry().register(new ItemBlock(WINDMILL_BIG).setRegistryName(WINDMILL_BIG.getRegistryName()));
        event.getRegistry().register(new ItemBlock(MECH_TORCH).setRegistryName(MECH_TORCH.getRegistryName()));
        event.getRegistry().register(new ItemBlock(MECH_TORCH_TOGGLE).setRegistryName(MECH_TORCH_TOGGLE.getRegistryName()));
        event.getRegistry().register(new ItemBlock(AXLE_WOOD).setRegistryName(AXLE_WOOD.getRegistryName()));
        event.getRegistry().register(new ItemBlock(RATIOBOX).setRegistryName(RATIOBOX.getRegistryName()));
        event.getRegistry().register(new ItemBlock(QUERN).setRegistryName(QUERN.getRegistryName()));
        event.getRegistry().register(new ItemBlock(GIN).setRegistryName(GIN.getRegistryName()));
        event.getRegistry().register(new ItemBlock(CRANK).setRegistryName(CRANK.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ASSEMBLER_1).setRegistryName(ASSEMBLER_1.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ASSEMBLER_2).setRegistryName(ASSEMBLER_2.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ASSEMBLER_3).setRegistryName(ASSEMBLER_3.getRegistryName()));
        event.getRegistry().register(new ItemBlock(COTTON_SEED).setRegistryName(COTTON_SEED.getRegistryName()));

        event.getRegistry().register(SHAMSHIR = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"shamshir")).setUnlocalizedName("shamshir").setCreativeTab(CreativeTabs.COMBAT));
        event.getRegistry().register(SPEAR = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"spear")).setUnlocalizedName("spear").setCreativeTab(CreativeTabs.COMBAT));
        event.getRegistry().register(BLASTSPEAR = new ItemBlastSpear().setRegistryName(new ResourceLocation(Rustichromia.MODID,"blastspear")).setUnlocalizedName("blastspear").setCreativeTab(CreativeTabs.COMBAT));
        event.getRegistry().register(WINDMILL_BLADE = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"windmill_blade")).setUnlocalizedName("windmill_blade").setCreativeTab(mystmechTab));
        event.getRegistry().register(GEAR_SPECKLED = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"gear_speckled")).setUnlocalizedName("gear_speckled").setCreativeTab(mystmechTab));
        event.getRegistry().register(GEAR_WOOD = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"gear_wood")).setUnlocalizedName("gear_wood").setCreativeTab(mystmechTab));
        event.getRegistry().register(DISK_STONE = new ItemDisk(1).setRegistryName(new ResourceLocation(Rustichromia.MODID,"disk_stone")).setUnlocalizedName("disk_stone").setCreativeTab(mystmechTab));
        event.getRegistry().register(DISK_SANDSTONE = new ItemDisk(1).setRegistryName(new ResourceLocation(Rustichromia.MODID,"disk_sandstone")).setUnlocalizedName("disk_sandstone").setCreativeTab(mystmechTab));
        event.getRegistry().register(DISK_RED_SANDSTONE = new ItemDisk(1).setRegistryName(new ResourceLocation(Rustichromia.MODID,"disk_red_sandstone")).setUnlocalizedName("disk_red_sandstone").setCreativeTab(mystmechTab));
        event.getRegistry().register(COTTON_CANDY = new ItemFood(4,2f, false).setRegistryName(new ResourceLocation(Rustichromia.MODID,"cotton_candy")).setUnlocalizedName("cotton_candy").setCreativeTab(CreativeTabs.FOOD));
        event.getRegistry().register(COTTON_CANDY_STICK = new ItemCottonCandy(6, 1.6f, false).setRegistryName(new ResourceLocation(Rustichromia.MODID,"cotton_candy_stick")).setUnlocalizedName("cotton_candy_stick").setCreativeTab(CreativeTabs.FOOD));
        event.getRegistry().register(COTTON = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"cotton")).setUnlocalizedName("cotton").setCreativeTab(CreativeTabs.MATERIALS));
        event.getRegistry().register(COTTON_WOOL = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"cotton_wool")).setUnlocalizedName("cotton_wool").setCreativeTab(CreativeTabs.MATERIALS));
        event.getRegistry().register(DUST_WOOD = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"dust_wood")).setUnlocalizedName("dust_wood").setCreativeTab(CreativeTabs.MATERIALS));
        event.getRegistry().register(PLATE_WOOD = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"plate_wood")).setUnlocalizedName("plate_wood").setCreativeTab(CreativeTabs.MATERIALS));
        event.getRegistry().register(DUST_FLOUR = new Item().setRegistryName(new ResourceLocation(Rustichromia.MODID,"dust_flour")).setUnlocalizedName("dust_flour").setCreativeTab(CreativeTabs.MATERIALS));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void registerOreDict(RegistryEvent.Register<IRecipe> event) {
        OreDictionary.registerOre("dustWood", DUST_WOOD);
        OreDictionary.registerOre("plateWood", PLATE_WOOD);
        OreDictionary.registerOre("gearWood", GEAR_WOOD);
        OreDictionary.registerOre("gearDiorite", GEAR_SPECKLED);
        OreDictionary.registerOre("gemCoal", new ItemStack(Items.COAL,1,0));
        OreDictionary.registerOre("gemCoal", new ItemStack(Items.COAL,1,1));
        OreDictionary.registerOre("dustFlour", DUST_FLOUR);
        OreDictionary.registerOre("foodFlour", DUST_FLOUR);
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
        registerItemModel(Item.getItemFromBlock(MECH_TORCH_TOGGLE), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(MECH_TORCH_TOGGLE), 1, "dial");
        registerItemModel(Item.getItemFromBlock(AXLE_WOOD), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(AXLE_WOOD), 1, "normal");
        registerItemModel(Item.getItemFromBlock(RATIOBOX), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(RATIOBOX), 1, "axle_input");
        registerItemModel(Item.getItemFromBlock(RATIOBOX), 2, "axle_output_a");
        registerItemModel(Item.getItemFromBlock(RATIOBOX), 3, "axle_output_b");
        registerItemModel(Item.getItemFromBlock(QUERN), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(QUERN), 1, "grindstone");
        registerItemModel(Item.getItemFromBlock(GIN), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(GIN), 1, "blade");
        registerItemModel(Item.getItemFromBlock(ASSEMBLER_1), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(ASSEMBLER_2), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(ASSEMBLER_3), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(COTTON_SEED), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(CRANK), 0, "inventory");
        registerItemModel(Item.getItemFromBlock(CRANK), 1, "normal");


        registerItemModel(WINDMILL_BLADE, 0, "inventory");
        registerItemModel(GEAR_SPECKLED, 0, "inventory");
        registerItemModel(GEAR_WOOD, 0, "inventory");
        registerItemModel(DISK_STONE, 0, "inventory");
        registerItemModel(DISK_SANDSTONE, 0, "inventory");
        registerItemModel(DISK_RED_SANDSTONE, 0, "inventory");
        registerItemModel(SHAMSHIR, 0, "inventory");
        registerItemModel(SPEAR, 0, "inventory");
        registerItemModel(SPEAR, 1, "normal");
        registerItemModel(BLASTSPEAR, 0, "inventory");
        registerItemModel(COTTON_CANDY, 0, "inventory");
        registerItemModel(COTTON_CANDY_STICK, 0, "inventory");
        registerItemModel(COTTON, 0, "inventory");
        registerItemModel(COTTON_WOOL, 0, "inventory");
        registerItemModel(DUST_WOOD, 0, "inventory");
        registerItemModel(PLATE_WOOD, 0, "inventory");
        registerItemModel(DUST_FLOUR, 0, "inventory");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModel(@Nonnull Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event) {
        event.getMap().registerSprite(new ResourceLocation(Rustichromia.MODID,"blocks/cotton"));
        event.getMap().registerSprite(new ResourceLocation(Rustichromia.MODID,"blocks/cotton_candy"));
    }
}
