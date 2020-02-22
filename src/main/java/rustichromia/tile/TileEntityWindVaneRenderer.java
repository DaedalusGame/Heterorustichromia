package rustichromia.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import rustichromia.block.BlockWindVane;

public class TileEntityWindVaneRenderer extends TileEntitySpecialRenderer<TileEntityWindVane> {
    @Override
    public void render(TileEntityWindVane tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        BlockWindVane block = (BlockWindVane) tile.getBlockType();
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "normal"));

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        double angle = tile.angle;
        double lastAngle = tile.lastAngle;
        float rotationAngle = (float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle;

        GlStateManager.translate(0.5, 0.5, 0.5);
        GlStateManager.rotate( rotationAngle, 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.popMatrix();
    }
}
