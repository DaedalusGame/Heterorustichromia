package rustichromia.recipe;

import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import rustichromia.Registry;
import rustichromia.Rustichromia;
import rustichromia.util.IngredientSized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static AssemblerRecipe getAssemblerRecipe(TileEntity tile, int tier, double power, List<ItemStack> inputs){
        for (int i = 0; i < assemblerRecipes.size(); i ++){
            AssemblerRecipe recipe = assemblerRecipes.get(i);
            if (recipe.tier <= tier && recipe.matches(tile,power,inputs)){
                return recipe;
            }
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
        event.getRegistry().register(new ShapedOreRecipe(getRL("disk_stone"),new ItemStack(Registry.DISK_STONE,8),true,new Object[]{
                "CCC", "CSC", "CCC",
                'C', "stone",
                'S', "nuggetIron"}).setRegistryName(getRL("disk_stone")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("disk_sandstone"),new ItemStack(Registry.DISK_SANDSTONE,8),true,new Object[]{
                "CCC", "CSC", "CCC",
                'C', new ItemStack(Blocks.SANDSTONE, 1, WILDCARD_VALUE),
                'S', "nuggetIron"}).setRegistryName(getRL("disk_sandstone")));
        event.getRegistry().register(new ShapedOreRecipe(getRL("disk_red_sandstone"),new ItemStack(Registry.DISK_RED_SANDSTONE,8),true,new Object[]{
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
        event.getRegistry().register(new ShapedOreRecipe(getRL("plate_wood"),new ItemStack(Registry.PLATE_WOOD,1),true,new Object[]{
                "WW", "WW",
                'W', "dustWood"}).setRegistryName(getRL("plate_wood")));

        quernRecipes.add(new QuernRecipe(Lists.newArrayList(Ingredient.fromItem(Items.REEDS)),Lists.newArrayList(new ItemStack(Items.SUGAR,2)),0, Double.POSITIVE_INFINITY,300));
        quernRecipes.add(new QuernRecipe(Lists.newArrayList(Ingredient.fromItem(Items.BONE)),Lists.newArrayList(new ItemStack(Items.DYE,6, 15)),3, Double.POSITIVE_INFINITY,900));
        quernRecipes.add(new QuernRecipe(Lists.newArrayList(new OreIngredient("plankWood")),Lists.newArrayList(new ItemStack(Registry.DUST_WOOD,2)),3, Double.POSITIVE_INFINITY,900));
        quernRecipes.add(new QuernRecipe(Lists.newArrayList(new OreIngredient("logWood")),Lists.newArrayList(new ItemStack(Registry.DUST_WOOD,10)),5, Double.POSITIVE_INFINITY,1800));
        quernRecipes.add(new QuernRecipe(Lists.newArrayList(new OreIngredient("cobblestone")),Lists.newArrayList(new ItemStack(Blocks.GRAVEL,1)),5, Double.POSITIVE_INFINITY,3000));
        quernRecipes.add(new QuernRecipe(Lists.newArrayList(new OreIngredient("gravel")),Lists.newArrayList(new ItemStack(Blocks.SAND,1), new ItemStack(Items.FLINT, 1)),10, Double.POSITIVE_INFINITY,3000));

        ginRecipes.add(new GinRecipe(Lists.newArrayList(new IngredientSized(Ingredient.fromItem(Items.SUGAR),3)),Lists.newArrayList(new ItemStack(Registry.COTTON_CANDY,1)),Lists.newArrayList(),7, Double.POSITIVE_INFINITY,3000));
        ginRecipes.add(new GinRecipe(Lists.newArrayList(Ingredient.fromItem(Registry.COTTON)),Lists.newArrayList(new ItemStack(Registry.COTTON_WOOL,1)),Lists.newArrayList(new ItemStack(Registry.COTTON_SEED)),3, Double.POSITIVE_INFINITY,300));

        ginFills.put(Registry.COTTON_CANDY, new ResourceLocation(Rustichromia.MODID,"blocks/cotton_candy"));
        ginFills.put(Registry.COTTON_WOOL, new ResourceLocation(Rustichromia.MODID,"blocks/cotton"));

        assemblerRecipes.add(new AssemblerRecipe(1,Lists.newArrayList(new OreIngredient("dustWood")),Lists.newArrayList(new ItemStack(Registry.PLATE_WOOD,1)),1, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(1,Lists.newArrayList(new IngredientSized(new OreIngredient("plateWood"),4)),Lists.newArrayList(new ItemStack(Registry.GEAR_WOOD,1)),1, Double.POSITIVE_INFINITY,500));
        assemblerRecipes.add(new AssemblerRecipe(1,Lists.newArrayList(new IngredientSized(new OreIngredient("plankWood"),2)),Lists.newArrayList(new ItemStack(Registry.AXLE_WOOD,8)),3, Double.POSITIVE_INFINITY,500));
    }

    private ResourceLocation getRL(String name) {
        return new ResourceLocation(Rustichromia.MODID,name);
    }
}
