package rustichromia.compat.jei.wrapper;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeWrapper;
import mysticalmechanics.api.IMechUnit;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import rustichromia.recipe.BasicMachineRecipe;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicMachineRecipeWrapper<TRecipe extends BasicMachineRecipe> implements IRecipeWrapper {
    public TRecipe recipe;
    public IDrawable gear;
    public IDrawable info;

    public BasicMachineRecipeWrapper(TRecipe recipe) {
        this.recipe = recipe;
    }

    protected List<String> getPowerTooltip() {
        List<String> tooltip = new ArrayList<>();
        List<String> basePowerData = recipe.getBasePowerData();
        List<String> powerData = recipe.getPowerData();
        if(basePowerData != null)
            tooltip.addAll(basePowerData);
        if(powerData != null)
            tooltip.addAll(powerData);
        boolean showAdvanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips || GuiScreen.isShiftKeyDown();
        if(showAdvanced)
            tooltip.add(TextFormatting.GRAY + recipe.id.toString());
        return tooltip.isEmpty() ? null : tooltip;
    }

    protected List<String> getExtraTooltip(){
        return recipe.getExtraData();
    }

    protected void drawGear(Minecraft minecraft, int gearX, int gearY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(gearX, gearY, 0);
        GlStateManager.translate(8, 8, 0);
        long l = System.currentTimeMillis() % 360000;
        double speed = recipe.getVisiblePower();
        double angle = l / (1000 / 20.0) * speed;
        GlStateManager.rotate((float) angle, 0, 0, 1);
        GlStateManager.translate(-8, -8, 0);
        gear.draw(minecraft, 0, 0);
        GlStateManager.popMatrix();
    }

    protected void drawInfo(Minecraft minecraft, int infoX, int infoY) {
        if(recipe.getExtraData() == null)
            return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(infoX, infoY, 0);
        info.draw(minecraft, 0, 0);
        GlStateManager.popMatrix();
    }
}
