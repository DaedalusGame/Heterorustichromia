package rustichromia.compat.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.compat.jei.JEI;
import rustichromia.compat.jei.ResultHelper;
import rustichromia.compat.jei.wrapper.GinWrapper;
import rustichromia.util.Misc;

import java.util.List;

public class GinCategory implements IRecipeCategory<GinWrapper> {
    private final IDrawable background;
    private final String name;
    public static final String UID = "rustichromia.gin";

    public static ResourceLocation texture = new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_gin.png");

    public IDrawable gear;

    public GinCategory(IGuiHelper helper){

        this.background = helper.createDrawable(texture, 0, 0, 91, 80);
        this.name = I18n.format("rustichromia.jei.recipe.gin");
        this.gear = helper.createDrawable(new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_gin.png"), 92, 0, 16, 16);
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
    public void setRecipe(IRecipeLayout recipeLayout, GinWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        ResultHelper helper = new ResultHelper();
        helper.setup(recipeWrapper.getOutputsInterior());
        helper.setup(recipeWrapper.getOutputsExterior());
        stacks.addTooltipCallback(helper.getTooltipCallback());

        List<List<ItemStack>> itemInputs = JEI.expandIngredients(recipeWrapper.getInputs());
        List<List<ItemStack>> itemOutputsInterior = helper.splitIntoBoxes(recipeWrapper.getOutputsInterior(),4);
        List<List<ItemStack>> itemOutputsExterior = helper.splitIntoBoxes(recipeWrapper.getOutputsExterior(),4);

        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 2; j++) {
                int index = i * 2 + j;
                stacks.init(index, true, 28+j*18, 2+i*18);
                if(itemInputs.size() > index)
                stacks.set(index,itemInputs.get(index));
            }

        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 2; j++) {
                int index = i * 2 + j;
                stacks.init(4+index, false, 1+j*18, 43+i*18);
                if(itemOutputsInterior.size() > index)
                    stacks.set(4+index,itemOutputsInterior.get(index));
            }

        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 2; j++) {
                int index = i * 2 + j;
                stacks.init(8+index, false, 55+j*18, 43+i*18);
                if(itemOutputsExterior.size() > index)
                    stacks.set(8+index,itemOutputsExterior.get(index));
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
