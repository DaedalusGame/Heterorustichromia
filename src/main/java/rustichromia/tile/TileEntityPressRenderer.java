package rustichromia.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import rustichromia.block.BlockPress;

public class TileEntityPressRenderer extends TileEntitySpecialRenderer<TileEntityPress> {
    @Override
    public void render(TileEntityPress tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        BlockPress block = (BlockPress) tile.getBlockType();
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel extension = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "extension"));
        IBakedModel head = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "head"));

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        double extend = tile.extend * partialTicks + tile.lastExtend * (1-partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -extend, 0);
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(head, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -extend/2, 0);
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(extension, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

}
