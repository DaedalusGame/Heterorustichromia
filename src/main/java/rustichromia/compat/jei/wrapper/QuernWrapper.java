package rustichromia.compat.jei.wrapper;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.compat.jei.JEI;
import rustichromia.recipe.QuernRecipe;

import java.util.ArrayList;
import java.util.List;

public class QuernWrapper extends BasicMachineRecipeWrapper<QuernRecipe> {
    public static final int GEAR_X = 38;
    public static final int GEAR_Y = 41;
    public static final int INFO_X = 74;
    public static final int INFO_Y = 62;

    public QuernWrapper(QuernRecipe recipe){
        super(recipe);
        this.gear = JEI.HELPER.getGuiHelper().createDrawable(new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_quern.png"), 92, 0, 16, 16);
        this.info = JEI.HELPER.getGuiHelper().createDrawable(new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_quern.png"), 92, 16, 16, 16);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> outputStacks = recipe.outputs;
        List<List<ItemStack>> inputStacks = JEI.expandIngredients(recipe.inputs);
        ingredients.setInputLists(ItemStack.class, inputStacks);
        ingredients.setOutputLists(ItemStack.class, Lists.<List<ItemStack>>newArrayList(outputStacks));
    }

    public List<ItemStack> getOutputs() {
        return new ArrayList<>(recipe.outputs);
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
