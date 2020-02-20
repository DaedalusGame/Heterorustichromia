package rustichromia.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.block.BlockGin;
import rustichromia.util.RenderUtil;

public class TileEntityGinRenderer extends TileEntitySpecialRenderer<TileEntityGin> {
    @Override
    public void render(TileEntityGin tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        BlockGin block = (BlockGin) tile.getBlockType();
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "blade"));

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        double angle = tile.angle;
        double lastAngle = tile.lastAngle;
        float rotationAngle = (float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle;
        float offset = 0;

        GlStateManager.pushMatrix();

        GlStateManager.translate(0.5, 0.5, 0.5);
        switch(tile.getFacing()) {
            case EAST:
            case WEST:
                GlStateManager.rotate(-90, 0, 1, 0);
                GlStateManager.rotate(rotationAngle + offset, 1, 0, 0);
                break;
            case NORTH:
            case SOUTH:
                GlStateManager.rotate(rotationAngle + offset, 1, 0, 0);
                break;
        }
        GlStateManager.translate(-0.5, -0.5, -0.5);

        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.popMatrix();

        float fill = tile.getFill();
        ResourceLocation texture = tile.getFillTexture();

        if(fill > 0) {
            float height = fill * 13;
            RenderUtil.renderTextureCuboid(texture, tile.getPos(), 1 / 16f, 1.1f / 16f, 1 / 16f, 15 / 16f, (1 + height) / 16f, 15 / 16f, 0xFFFFFFFF);
        }
        GlStateManager.popMatrix();
    }

}
