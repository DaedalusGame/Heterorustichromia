package rustichromia.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import rustichromia.Registry;
import rustichromia.compat.jei.category.AssemblerCategory;
import rustichromia.compat.jei.category.GinCategory;
import rustichromia.compat.jei.category.QuernCategory;
import rustichromia.compat.jei.wrapper.AssemblerWrapper;
import rustichromia.compat.jei.wrapper.GinWrapper;
import rustichromia.compat.jei.wrapper.QuernWrapper;
import rustichromia.recipe.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class JEI implements IModPlugin {
    public static IJeiHelpers HELPER;

    public static AssemblerCategory ASSEMBLER_1;
    public static AssemblerCategory ASSEMBLER_2;
    public static AssemblerCategory ASSEMBLER_3;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new QuernCategory(guiHelper));
        registry.addRecipeCategories(new GinCategory(guiHelper));
        registry.addRecipeCategories(ASSEMBLER_1 = new AssemblerCategory(guiHelper,1));
        registry.addRecipeCategories(ASSEMBLER_2 = new AssemblerCategory(guiHelper,2));
        registry.addRecipeCategories(ASSEMBLER_3 = new AssemblerCategory(guiHelper,3));
    }

    @Override
    public void register(IModRegistry reg)
    {
        HELPER = reg.getJeiHelpers();

        reg.handleRecipes(QuernRecipe.class, QuernWrapper::new,QuernCategory.UID);
        reg.handleRecipes(GinRecipe.class, GinWrapper::new,GinCategory.UID);
        reg.handleRecipes(AssemblerRecipe.class, AssemblerWrapper::new,ASSEMBLER_1.getUid());
        reg.handleRecipes(AssemblerRecipe.class, AssemblerWrapper::new,ASSEMBLER_2.getUid());
        reg.handleRecipes(AssemblerRecipe.class, AssemblerWrapper::new,ASSEMBLER_3.getUid());

        reg.addRecipes(expandRecipes(RecipeRegistry.quernRecipes),QuernCategory.UID);
        reg.addRecipes(expandRecipes(RecipeRegistry.ginRecipes),GinCategory.UID);
        reg.addRecipes(expandRecipes(RecipeRegistry.getAssemblerRecipes(1)),ASSEMBLER_1.getUid());
        reg.addRecipes(expandRecipes(RecipeRegistry.getAssemblerRecipes(2)),ASSEMBLER_2.getUid());
        reg.addRecipes(expandRecipes(RecipeRegistry.getAssemblerRecipes(3)),ASSEMBLER_3.getUid());

        reg.addRecipeCatalyst(new ItemStack(Registry.QUERN),QuernCategory.UID);
        reg.addRecipeCatalyst(new ItemStack(Registry.GIN),GinCategory.UID);
        reg.addRecipeCatalyst(new ItemStack(Registry.ASSEMBLER_1),ASSEMBLER_1.getUid());
        reg.addRecipeCatalyst(new ItemStack(Registry.ASSEMBLER_2),ASSEMBLER_1.getUid());
        reg.addRecipeCatalyst(new ItemStack(Registry.ASSEMBLER_3),ASSEMBLER_1.getUid());
        reg.addRecipeCatalyst(new ItemStack(Registry.ASSEMBLER_2),ASSEMBLER_2.getUid());
        reg.addRecipeCatalyst(new ItemStack(Registry.ASSEMBLER_3),ASSEMBLER_2.getUid());
        reg.addRecipeCatalyst(new ItemStack(Registry.ASSEMBLER_3),ASSEMBLER_3.getUid());
    }

    public static List<List<ItemStack>> expandIngredients(Ingredient ingredient) {
        return expandIngredients(Lists.newArrayList(ingredient));
    }

    public static List<List<ItemStack>> expandIngredients(List<Ingredient> ingredients) {
        IStackHelper stackHelper = HELPER.getStackHelper();
        return stackHelper.expandRecipeItemStackInputs(ingredients);
    }

    public static List<Object> expandRecipes(List<?> recipes) {
        return recipes.stream().map(JEI::expandRecipe).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<?> expandRecipe(Object recipe) {
        if(recipe instanceof IWrappableRecipe)
            return ((IWrappableRecipe) recipe).getWrappers();
        else
            return Lists.newArrayList(recipe);
    }
}
