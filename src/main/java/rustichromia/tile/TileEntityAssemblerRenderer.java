package rustichromia.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import rustichromia.block.BlockQuern;
import rustichromia.recipe.AssemblerRecipe;
import rustichromia.recipe.RecipeRegistry;

public class TileEntityAssemblerRenderer extends TileEntitySpecialRenderer<TileEntityAssembler> {
    @Override
    public void render(TileEntityAssembler tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        ItemStack gear = tile.getDisplayGear();
        double offset = -6.5 / 16.0;
        double angle = tile.angle;
        double lastAngle = tile.lastAngle;
        float rotationAngle = (float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle;

        for (EnumFacing direction : EnumFacing.HORIZONTALS) {
            if(direction.equals(tile.getFacing()))
                continue;
            GlStateManager.pushMatrix();
            GlStateManager.translate( 0.5, 0.5, 0.5);

            switch (direction) {
                case NORTH:
                    break;
                case WEST:
                    GlStateManager.rotate(90, 0, 1, 0);
                    break;
                case SOUTH:
                    GlStateManager.rotate(180, 0, 1, 0);
                    break;
                case EAST:
                    GlStateManager.rotate(270, 0, 1, 0);
                    break;
                default:
                    break;
            }

            GlStateManager.color(0.5f,0.5f,0.5f,0.5f);
            GlStateManager.translate(0, 0, offset);
            GlStateManager.scale(0.875, 0.875, 0.875);
            GlStateManager.rotate(direction.getAxis() == EnumFacing.Axis.X ? rotationAngle : -rotationAngle, 0, 0, 1);
            Minecraft.getMinecraft().getRenderItem().renderItem(gear,
                    ItemCameraTransforms.TransformType.FIXED);

            GlStateManager.popMatrix();
        }

        ItemStack displayItem = ItemStack.EMPTY;
        for (AssemblerRecipe recipe : RecipeRegistry.assemblerRecipes) {
            if(recipe.id.equals(tile.filter)) {
                displayItem = recipe.getIcon();
                break;
            }
        }
        if(!displayItem.isEmpty()) {
            GlStateManager.pushAttrib();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.pushMatrix();
            GlStateManager.translate( 0.5, 0.75+0.2, 0.5);
            GlStateManager.scale(0.75, 0.75, 0.75);
            GlStateManager.rotate((float) MathHelper.clampedLerp(tile.lastAngle, tile.angle, partialTicks), 0, 1, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(displayItem, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }

        GlStateManager.popMatrix();
    }

}
