package rustichromia.compat.jei.wrapper;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.compat.jei.JEI;
import rustichromia.recipe.GinRecipe;
import rustichromia.util.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GinWrapper extends BasicMachineRecipeWrapper<GinRecipe> {
    public static final int GEAR_X = 38;
    public static final int GEAR_Y = 41;
    public static final int INFO_X = 38;
    public static final int INFO_Y = 63;

    public GinWrapper(GinRecipe recipe){
        super(recipe);
        this.gear = JEI.HELPER.getGuiHelper().createDrawable(new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_gin.png"), 92, 0, 16, 16);
        this.info = JEI.HELPER.getGuiHelper().createDrawable(new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_gin.png"), 92, 16, 16, 16);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> outputStacksExterior = recipe.outputsExterior.stream().map(Result::getJEIStack).collect(Collectors.toList());
        List<ItemStack> outputStacksInterior = recipe.outputsInterior.stream().map(Result::getJEIStack).collect(Collectors.toList());
        List<List<ItemStack>> inputStacks = JEI.expandIngredients(recipe.inputs);
        ingredients.setInputLists(ItemStack.class, inputStacks);
        ingredients.setOutputLists(ItemStack.class, Lists.<List<ItemStack>>newArrayList(outputStacksInterior,outputStacksExterior));
    }

    public List<Result> getOutputsExterior() {
        return recipe.outputsExterior;
    }

    public List<Result> getOutputsInterior() {
        return recipe.outputsInterior;
    }

    public List<Ingredient> getInputs() {
        return new ArrayList<>(recipe.inputs);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= GEAR_X && mouseY >= GEAR_Y && mouseX < GEAR_X + 16 && mouseY < GEAR_Y + 16) {
            return getPowerTooltip();
        }
        if (mouseX >= INFO_X && mouseY >= INFO_Y && mouseX < INFO_X + 16 && mouseY < INFO_Y + 16) {
            return getExtraTooltip();
        }
        return null;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        drawGear(minecraft, GEAR_X, GEAR_Y);
        drawInfo(minecraft, INFO_X, INFO_Y);
    }
}
