package rustichromia.compat.jei.wrapper;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.compat.jei.JEI;
import rustichromia.recipe.AssemblerRecipe;
import rustichromia.util.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblerWrapper extends BasicMachineRecipeWrapper<AssemblerRecipe> {
    public static final int GEAR_X = 60;
    public static final int GEAR_Y = 3;
    public static final int INFO_X = 60;
    public static final int INFO_Y = 40;

    public AssemblerWrapper(AssemblerRecipe recipe) {
        super(recipe);
        this.gear = JEI.HELPER.getGuiHelper().createDrawable(new ResourceLocation(Rustichromia.MODID, "textures/gui/jei_assembler.png"), 135, 0, 16, 16);
        this.info = JEI.HELPER.getGuiHelper().createDrawable(new ResourceLocation(Rustichromia.MODID, "textures/gui/jei_assembler.png"), 135, 16, 16, 16);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> outputStacks = recipe.outputs.stream().map(Result::getJEIStack).collect(Collectors.toList());
        List<List<ItemStack>> inputStacks = JEI.expandIngredients(recipe.inputs);
        ingredients.setInputLists(ItemStack.class, inputStacks);
        ingredients.setOutputLists(ItemStack.class, Lists.<List<ItemStack>>newArrayList(outputStacks));
    }

    public List<Result> getOutputs() {
        return recipe.outputs;
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
