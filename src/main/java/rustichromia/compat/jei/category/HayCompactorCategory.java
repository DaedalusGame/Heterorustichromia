package rustichromia.compat.jei.category;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.compat.jei.JEI;
import rustichromia.compat.jei.ResultHelper;
import rustichromia.compat.jei.wrapper.HayCompactorWrapper;
import rustichromia.compat.jei.wrapper.QuernWrapper;
import rustichromia.util.IngredientSized;
import rustichromia.util.Misc;
import rustichromia.util.Result;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class HayCompactorCategory implements IRecipeCategory<HayCompactorWrapper> {
    private final IDrawable background;
    private final String name;
    public static final String UID = "rustichromia.haycompactor";

    public static ResourceLocation texture = new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_haycompactor.png");

    public IDrawable gear;

    public HayCompactorCategory(IGuiHelper helper){

        this.background = helper.createDrawable(texture, 0, 0, 91, 80);
        this.name = I18n.format("rustichromia.jei.recipe.haycompactor");
        this.gear = helper.createDrawable(new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_haycompactor.png"), 92, 0, 16, 16);
    }

    @Override
    public String getTitle()
    {
        return name;
    }

    @Override
    public IDrawable getBackground(){
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HayCompactorWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        ResultHelper helper = new ResultHelper();
        helper.setup(recipeWrapper.getOutputs());
        stacks.addTooltipCallback(helper.getTooltipCallback());

        List<List<ItemStack>> itemInputs = JEI.expandIngredients(recipeWrapper.getInputs());
        List<List<ItemStack>> itemOutputs = helper.splitIntoBoxes(recipeWrapper.getOutputs(), 3);

        for(int i = 0; i < 1; i++)
            for(int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                stacks.init(index, true, 19+j*18, 8+i*18);
                if(itemInputs.size() > index)
                stacks.set(index,itemInputs.get(index));
            }

        for(int i = 0; i < 1; i++)
            for(int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                stacks.init(4+index, false, 19+j*18, 61+i*18);
                if(itemOutputs.size() > index) {
                    stacks.set(4 + index, itemOutputs.get(index));
                }
            }
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getModName() {
        return Rustichromia.MODID;
    }
}
