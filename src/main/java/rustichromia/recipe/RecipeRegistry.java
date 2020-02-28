package rustichromia.recipe;

import com.google.common.collect.Lists;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import rustichromia.ConfigManager;
import rustichromia.Registry;
import rustichromia.Rustichromia;
import rustichromia.util.IngredientSet;
import rustichromia.util.IngredientSized;
import rustichromia.util.Misc;
import rustichromia.util.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public class RecipeRegistry {
    public static ArrayList<QuernRecipe> quernRecipes = new ArrayList<>();
    public static ArrayList<GinRecipe> ginRecipes = new ArrayList<>();
    public static ArrayList<AssemblerRecipe> assemblerRecipes = new ArrayList<>();
    public static HashMap<Item,ResourceLocation> ginFills = new HashMap<>();

    public static QuernRecipe getQuernRecipe(TileEntity tile, double power, List<ItemStack> inputs){
        for (int i = 0; i < quernRecipes.size(); i ++){
            QuernRecipe recipe = quernRecipes.get(i);
            if (recipe.matches(tile,power,inputs)){
                return recipe;
            }
        }
        return null;
    }

    public static GinRecipe getGinRecipe(TileEntity tile, double power, List<ItemStack> inputs){
        for (int i = 0; i < ginRecipes.size(); i ++){
            GinRecipe recipe = ginRecipes.get(i);
            if (recipe.matches(tile,power,inputs)){
                return recipe;
            }
        }
        return null;
    }

    public static AssemblerRecipe getAssemblerRecipe(TileEntity tile, int tier, double power, List<ItemStack> inputs, ResourceLocation filter){
        for (int i = 0; i < assemblerRecipes.size(); i ++){
            AssemblerRecipe recipe = assemblerRecipes.get(i);
            if(filter != null && !recipe.id.equals(filter))
                continue;
            if (recipe.tier <= tier && recipe.matches(tile,power,inputs))
                return recipe;
        }
        return null;
    }

    public static List<AssemblerRecipe> getAssemblerRecipes(int tier) {
        return assemblerRecipes.stream().filter(recipe -> recipe.tier == tier).collect(Collectors.toList());
    }

    public static ResourceLocation getGinFill(ItemStack stack) {
        return ginFills.get(stack.getItem());
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new RecipeCombine().setRegistryName(new ResourceLocation(Rustichromia.MODID,"combine")));

        event.getRegistry().register(new ShapedOreRecipe(getRL("axle_wood"),new ItemStack(Registry.AXLE_WOOD,2),true,new Object[]{
                "C", "S", "C",
                'C', "plankWood",
                'S', "stickWood"}).setRegistryName(getRL("axle_wood")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("gear_wood"),new ItemStack(Registry.GEAR_WOOD,1),true,new Object[]{
                " C ", "CSC", " C ",
                'C', "plateWood",
                'S', "stickWood"}).setRegistryName(getRL("gear_wood")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("gear_speckled"),new ItemStack(Registry.GEAR_SPECKLED,1),true,new Object[]{
                " C ", "CSC", " C ",
                'C', "stoneDiorite",
                'S', "nuggetIron"}).setRegistryName(getRL("gear_speckled")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("disk_stone"),new ItemStack(Registry.DISK_STONE,1),true,new Object[]{
                "CCC", "CSC", "CCC",
                'C', "stone",
                'S', "nuggetIron"}).setRegistryName(getRL("disk_stone")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("disk_sandstone"),new ItemStack(Registry.DISK_SANDSTONE,1),true,new Object[]{
                "CCC", "CSC", "CCC",
                'C', new ItemStack(Blocks.SANDSTONE, 1, WILDCARD_VALUE),
                'S', "nuggetIron"}).setRegistryName(getRL("disk_sandstone")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("disk_red_sandstone"),new ItemStack(Registry.DISK_RED_SANDSTONE,1),true,new Object[]{
                "CCC", "CSC", "CCC",
                'C', new ItemStack(Blocks.RED_SANDSTONE, 1, WILDCARD_VALUE),
                'S', "nuggetIron"}).setRegistryName(getRL("disk_red_sandstone")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("ratiobox"),new ItemStack(Registry.RATIOBOX,1),true,new Object[]{
                " L ", "WGW", " W ",
                'G', "gearWood",
                'L', new ItemStack(Blocks.LEVER),
                'W', "slabWood"}).setRegistryName(getRL("ratiobox")));

        event.getRegistry().register(new ShapedOreRecipe(getRL("windmill_blade"),new ItemStack(Registry.WINDMILL_BLADE,1),true,new Object[]{
                "WWI", "WW ",
                'I', "plankWood",
                'W', "slabWood"}).setRegistryName(getRL("windmill_blade")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("windmill"),new ItemStack(Registry.WINDMILL,1),true,new Object[]{
                "WW", "WW",
                'W', new ItemStack(Registry.WINDMILL_BLADE)}).setRegistryName(getRL("windmill")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("windmill_big"),new ItemStack(Registry.WINDMILL_BIG,1),true,new Object[]{
                " W ", "WGW", " W ",
                'W', new ItemStack(Registry.WINDMILL_BLADE),
                'G', "gearWood"}).setRegistryName(getRL("windmill_big")));

        event.getRegistry().register(new ShapedOreRecipe(getRL("mech_torch"),new ItemStack(Registry.MECH_TORCH,1),true,new Object[]{
                "T", "G", "I",
                'T', new ItemStack(Blocks.REDSTONE_TORCH),
                'I', new ItemStack(Registry.AXLE_WOOD),
                'G', "gearWood"}).setRegistryName(getRL("mech_torch")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("mech_torch_toggle"),new ItemStack(Registry.MECH_TORCH_TOGGLE,1),true,new Object[]{
                "T", "G", "I",
                'T', new ItemStack(Blocks.LEVER),
                'I', new ItemStack(Registry.AXLE_WOOD),
                'G', "gearWood"}).setRegistryName(getRL("mech_torch_toggle")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("plate_wood_early"),new ItemStack(Registry.PLATE_WOOD,1),true,new Object[]{
                "WWW", "WWW", "WWW",
                'W', "slabWood"}).setRegistryName(getRL("plate_wood_early")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("plate_wood"),new ItemStack(Registry.PLATE_WOOD,1),true,new Object[]{
                "WW", "WW",
                'W', "dustWood"}).setRegistryName(getRL("plate_wood")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("quern"),new ItemStack(Registry.QUERN,1),true,new Object[]{
                " I ", "SGS", "SSS",
                'I', new ItemStack(Registry.AXLE_WOOD),
                'G', "stone",
                'S', "cobblestone"}).setRegistryName(getRL("quern")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("gin"),new ItemStack(Registry.GIN,1),true,new Object[]{
                "WGW", "IHI", "WWW",
                'H', new ItemStack(Items.IRON_HOE,1, WILDCARD_VALUE),
                'I', new ItemStack(Registry.AXLE_WOOD),
                'W', "plankWood",
                'G', "paneGlassColorless"}).setRegistryName(getRL("gin")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("assembler1"),new ItemStack(Registry.ASSEMBLER_1,1),true,new Object[]{
                "WGW", "GGG", "WGW",
                'G', "gearWood",
                'W', "plankWood"}).setRegistryName(getRL("assembler1")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("assembler2"),new ItemStack(Registry.ASSEMBLER_2,1),true,new Object[]{
                "WGW", "GGG", "WGW",
                'G', "gearIron",
                'W', "ingotIron"}).setRegistryName(getRL("assembler2")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("assembler3"),new ItemStack(Registry.ASSEMBLER_3,1),true,new Object[]{
                "WGW", "GGG", "WGW",
                'G', "gearGold",
                'W', "ingotIron"}).setRegistryName(getRL("assembler3")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("crank"),new ItemStack(Registry.CRANK,1),true,new Object[]{
                " I", "II", "I ",
                'I', new ItemStack(Registry.AXLE_WOOD)}).setRegistryName(getRL("crank")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("windvane"),new ItemStack(Registry.WINDVANE,1),true,new Object[]{
                "NI ", " II", " G ",
                'N', "nuggetIron",
                'I', "ingotIron",
                'G', new ItemStack(RegistryHandler.IRON_AXLE)}).setRegistryName(getRL("windvane")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("hopper_wood"),new ItemStack(Registry.HOPPER_WOOD,1),true,new Object[]{
                "W W", "WGW", " W ",
                'W', "slabWood",
                'G', "gearWood"}).setRegistryName(getRL("hopper_wood")));
        event.getRegistry().register(new ShapelessOreRecipe(getRL("cotton_candy_stick"),new ItemStack(Registry.COTTON_CANDY_STICK,1),new Object[]{
                "stickWood",new ItemStack(Registry.COTTON_CANDY),new ItemStack(Registry.COTTON_CANDY)}).setRegistryName(getRL("cotton_candy_stick")));

        quernRecipes.add(new QuernRecipe(getRL("reeds_to_sugar"),Lists.newArrayList(Ingredient.fromItem(Items.REEDS)),Lists.newArrayList(new ItemStack(Items.SUGAR,2)),0, Double.POSITIVE_INFINITY,300));
        quernRecipes.add(new QuernRecipe(getRL("wheat_to_chaff"),Lists.newArrayList(Ingredient.fromItem(Items.WHEAT)),Lists.newArrayList(new ItemStack(Items.WHEAT_SEEDS),new ItemStack(Registry.WHEAT_CHAFF)),0, Double.POSITIVE_INFINITY,300)); //TODO: make this produce chaff; possible candidate for Gin (threshing)
        quernRecipes.add(new QuernRecipe(getRL("wheat_to_flour"),Lists.newArrayList(Ingredient.fromItem(Items.WHEAT_SEEDS)),Lists.newArrayList(new ItemStack(Registry.DUST_FLOUR)),0, Double.POSITIVE_INFINITY,300));
        quernRecipes.add(new QuernRecipe(getRL("bonemeal"),Lists.newArrayList(Ingredient.fromItem(Items.BONE)),Lists.newArrayList(new ItemStack(Items.DYE,6, 15)),1, Double.POSITIVE_INFINITY,450));
        quernRecipes.add(new QuernRecipe(getRL("slab_to_dust"),Lists.newArrayList(new OreIngredient("slabWood")),Lists.newArrayList(new ItemStack(Registry.DUST_WOOD,1)),1, Double.POSITIVE_INFINITY, 900));
        quernRecipes.add(new QuernRecipe(getRL("plank_to_dust"),Lists.newArrayList(new OreIngredient("plankWood")),Lists.newArrayList(new ItemStack(Registry.DUST_WOOD,2)),1, Double.POSITIVE_INFINITY, 1800));
        quernRecipes.add(new QuernRecipe(getRL("log_to_dust"),Lists.newArrayList(new OreIngredient("logWood")),Lists.newArrayList(new ItemStack(Registry.DUST_WOOD,10)),3, Double.POSITIVE_INFINITY, 3000));
        quernRecipes.add(new QuernRecipe(getRL("cobblestone_to_gravel"),Lists.newArrayList(new OreIngredient("cobblestone")),Lists.newArrayList(new ItemStack(Blocks.GRAVEL,1)),5, Double.POSITIVE_INFINITY,3000));
        quernRecipes.add(new QuernRecipe(getRL("gravel_to_sand"),Lists.newArrayList(new OreIngredient("gravel")),Lists.newArrayList(new ItemStack(Blocks.SAND,1), new ItemStack(Items.FLINT, 1)),10, Double.POSITIVE_INFINITY,3000));
        quernRecipes.add(new QuernRecipe(getRL("blaze_powder"),Lists.newArrayList(Ingredient.fromItem(Items.BLAZE_ROD)),Lists.newArrayList(new ItemStack(Items.BLAZE_POWDER,5)),10, Double.POSITIVE_INFINITY,1500));
        if(ConfigManager.quernOreAmount > 0) {
            quernRecipes.add(new QuernRecipe(getRL("coal"),Lists.newArrayList(new OreIngredient("oreCoal")), Lists.newArrayList(new ItemStack(Items.COAL, 2 * ConfigManager.quernOreAmount)), 20, Double.POSITIVE_INFINITY, 3000));
            quernRecipes.add(new QuernRecipe(getRL("redstone"),Lists.newArrayList(new OreIngredient("oreRedstone")), Lists.newArrayList(new ItemStack(Items.REDSTONE, 5 * ConfigManager.quernOreAmount)), 20, Double.POSITIVE_INFINITY, 3000));
            quernRecipes.add(new QuernRecipe(getRL("lapis"),Lists.newArrayList(new OreIngredient("oreLapis")), Lists.newArrayList(new ItemStack(Items.DYE, 8 * ConfigManager.quernOreAmount, 4)), 20, Double.POSITIVE_INFINITY, 3000));
            quernRecipes.add(new QuernRecipe(getRL("quartz"),Lists.newArrayList(new OreIngredient("oreQuartz")), Lists.newArrayList(new ItemStack(Items.QUARTZ, 2 * ConfigManager.quernOreAmount)), 20, Double.POSITIVE_INFINITY, 3000));
        }
        ginRecipes.add(new GinRecipe(getRL("cotton"),Lists.newArrayList(Ingredient.fromItem(Registry.COTTON)),Lists.newArrayList(new ItemStack(Registry.COTTON_WOOL,1)),Lists.newArrayList(new ItemStack(Registry.COTTON_SEED)),3, Double.POSITIVE_INFINITY,300));
        ginRecipes.add(new GinRecipe(getRL("cotton_candy"),Lists.newArrayList(new IngredientSized(Ingredient.fromItem(Items.SUGAR),3)),Lists.newArrayList(new ItemStack(Registry.COTTON_CANDY,1)),Lists.newArrayList(),7, Double.POSITIVE_INFINITY,3000));

        ginFills.put(Registry.COTTON_CANDY, new ResourceLocation(Rustichromia.MODID,"blocks/cotton_candy"));
        ginFills.put(Registry.COTTON_WOOL, new ResourceLocation(Rustichromia.MODID,"blocks/cotton"));

        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"stick_wood"),1,Lists.newArrayList(new IngredientSized(new OreIngredient("plankWood"),1)),Lists.newArrayList(new ItemStack(Items.STICK,2)),1, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"torch"),1,Lists.newArrayList(new OreIngredient("stickWood"),new OreIngredient("gemCoal")),Lists.newArrayList(new ItemStack(Blocks.TORCH,5)),1, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"plate_wood"),1,Lists.newArrayList(new OreIngredient("dustWood")),Lists.newArrayList(new ItemStack(Registry.PLATE_WOOD,1)),1, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"gear_wood"),1,Lists.newArrayList(new IngredientSized(new OreIngredient("plateWood"),4)),Lists.newArrayList(new ItemStack(Registry.GEAR_WOOD,1)),1, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"axle_wood"),1,Lists.newArrayList(new IngredientSized(new OreIngredient("plankWood"),2)),Lists.newArrayList(new ItemStack(Registry.AXLE_WOOD,8)),3, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"stonebrick_chiseled"),1,new IngredientSet().stack(new ItemStack(Blocks.STONEBRICK)), new ResultSet().stack(new ItemStack(Blocks.STONEBRICK,1, BlockStoneBrick.EnumType.CHISELED.getMetadata())),5, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"quartz_chiseled"),1,new IngredientSet().stack(new ItemStack(Blocks.QUARTZ_BLOCK)), new ResultSet().stack(new ItemStack(Blocks.QUARTZ_BLOCK,1, BlockQuartz.EnumType.CHISELED.getMetadata())),5, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"name_tag"),1,new IngredientSet().ore("paper",2).ore("string",1), new ResultSet().stack(new ItemStack(Items.NAME_TAG,1)),5, Double.POSITIVE_INFINITY,500));

        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"disk_stone"),2,Lists.newArrayList(new OreIngredient("nuggetIron"),new OreIngredient("stone")),Lists.newArrayList(new ItemStack(Registry.DISK_STONE,1)),5, Double.POSITIVE_INFINITY,1000));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"disk_sandstone"),2,Lists.newArrayList(new OreIngredient("nuggetIron"),Ingredient.fromStacks(new ItemStack(Blocks.SANDSTONE, 1, WILDCARD_VALUE))),Lists.newArrayList(new ItemStack(Registry.DISK_SANDSTONE,1)),5, Double.POSITIVE_INFINITY,1000));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"disk_red_sandstone"),2,Lists.newArrayList(new OreIngredient("nuggetIron"),Ingredient.fromStacks(new ItemStack(Blocks.RED_SANDSTONE, 1, WILDCARD_VALUE))),Lists.newArrayList(new ItemStack(Registry.DISK_RED_SANDSTONE,1)),5, Double.POSITIVE_INFINITY,1000));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"gear_iron"),2,Lists.newArrayList(new IngredientSized(new OreIngredient("ingotIron"),2)),Lists.newArrayList(new ItemStack(RegistryHandler.IRON_GEAR,1)),10, Double.POSITIVE_INFINITY,1500));
        assemblerRecipes.add(new AssemblerRecipe(new ResourceLocation(Rustichromia.MODID,"axle_iron"),2,Lists.newArrayList(new IngredientSized(new OreIngredient("ingotIron"),2)),Lists.newArrayList(new ItemStack(RegistryHandler.IRON_AXLE,8)),15, Double.POSITIVE_INFINITY,1500));

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(Registry.DUST_FLOUR),new ItemStack(Items.BREAD),0.1f);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void registerRecipesLate(RegistryEvent.Register<IRecipe> event) {
        addOreQuernRecipes();
        addFlowerQuernRecipes(event.getRegistry());
    }

    private void addOreQuernRecipes() {
        if(ConfigManager.quernOreAmount <= 0)
            return;
        for (String name : OreDictionary.getOreNames()) {
            if (!Misc.oreExists(name)) continue;

            if (name.startsWith("ore")) {
                String ore = name.substring("ore".length());
                if (Misc.isOreBlacklisted(ore)) continue;

                ItemStack output = ItemStack.EMPTY;

                if (Misc.oreExists("dust" + ore)) {
                    output = Misc.getOreStack("dust" + ore);
                    output.setCount(ConfigManager.quernOreAmount);
                } else if (Misc.oreExists("gem" + ore)) {
                    output = Misc.getOreStack("gem" + ore);
                    output.setCount(ConfigManager.quernOreAmount);
                }

                if(!output.isEmpty())
                    quernRecipes.add(new QuernRecipe(getRL("auto_"+ore.toLowerCase()),Lists.newArrayList(new OreIngredient(name)),Lists.newArrayList(output),20, Double.POSITIVE_INFINITY,3000));
            }
        }
    }

    private void addFlowerQuernRecipes(IForgeRegistry<IRecipe> recipes) {
        if (ConfigManager.quernFlowerAmount <= 0)
            return;
        int id = 0;
        for (Map.Entry<ResourceLocation, IRecipe> entry : recipes.getEntries()) {
            IRecipe recipe = entry.getValue();
            ItemStack output = recipe.getRecipeOutput();
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            if(Misc.oreStartsWith(output,"dye") && ingredients.size() == 1){
                //We got a dye, now for the less fun bit: find a flower ingredient
                for (Ingredient ingredient : ingredients) {
                    for (ItemStack flowerCandidate : ingredient.getMatchingStacks()) {
                        if(IsFlower(flowerCandidate)) {
                            output = output.copy();
                            output.setCount(output.getCount() * ConfigManager.quernFlowerAmount);
                            quernRecipes.add(new QuernRecipe(getRL("auto_dye"+id),Lists.newArrayList(Ingredient.fromStacks(flowerCandidate)),Lists.newArrayList(output),0, Double.POSITIVE_INFINITY,300));
                            id++;
                        }
                    }
                    break;
                }
            }
        }
    }

    private boolean IsFlower(ItemStack flowerCandidate) {
        Item flowerItem = flowerCandidate.getItem();
        if(flowerItem instanceof ItemBlock) {
            Block flowerBlock = ((ItemBlock) flowerItem).getBlock();
            Material material = flowerBlock.getDefaultState().getMaterial();
            if(material == Material.PLANTS || material == Material.CACTUS || material == Material.CORAL || material == Material.GOURD || material == Material.VINE){
                return true;
            }
        }
        return false;
    }

    private ResourceLocation getRL(String name) {
        return new ResourceLocation(Rustichromia.MODID,name);
    }
}
