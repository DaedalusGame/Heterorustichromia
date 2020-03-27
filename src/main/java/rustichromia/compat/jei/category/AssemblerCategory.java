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
import rustichromia.compat.jei.wrapper.AssemblerWrapper;
import rustichromia.util.Misc;

import java.util.List;

public class AssemblerCategory implements IRecipeCategory<AssemblerWrapper> {
    private final IDrawable background;
    private final String name;
    private static final String UID = "rustichromia.assembler";

    public static ResourceLocation texture = new ResourceLocation(Rustichromia.MODID,"textures/gui/jei_assembler.png");

    public int tier;

    public AssemblerCategory(IGuiHelper helper, int tier){

        this.background = helper.createDrawable(texture, 0, 0, 134, 57);
        this.tier = tier;
        this.name = I18n.format("rustichromia.jei.recipe.assembler"+tier);
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
    public void setRecipe(IRecipeLayout recipeLayout, AssemblerWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        ResultHelper helper = new ResultHelper();
        helper.setup(recipeWrapper.getOutputs());
        stacks.addTooltipCallback(helper.getTooltipCallback());

        List<List<ItemStack>> itemInputs = JEI.expandIngredients(recipeWrapper.getInputs());
        List<List<ItemStack>> itemOutputs = helper.splitIntoBoxes(recipeWrapper.getOutputs(),9);

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                stacks.init(index, true, 1+j*18, 2+i*18);
                if(itemInputs.size() > index)
                stacks.set(index,itemInputs.get(index));
            }

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                stacks.init(9+index, false, 80+j*18, 2+i*18);
                if(itemOutputs.size() > index)
                    stacks.set(9+index,itemOutputs.get(index));
            }
    }

    @Override
    public String getUid() {
        return UID+"."+tier;
    }

    @Override
    public String getModName() {
        return Rustichromia.MODID;
    }
}
