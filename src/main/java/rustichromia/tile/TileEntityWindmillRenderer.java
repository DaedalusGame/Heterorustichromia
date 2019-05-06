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
import rustichromia.block.BlockWindmill;

public class TileEntityWindmillRenderer extends TileEntitySpecialRenderer<TileEntityWindmill> {
    @Override
    public void render(TileEntityWindmill tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        BlockWindmill block = (BlockWindmill) tile.getBlockType();
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "blade"));

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        EnumFacing facing = tile.getFacing();

        double scale = tile.getScale();
        double angle = tile.angle;
        double lastAngle = tile.lastAngle;
        float rotationAngle = (float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle;

        int blades = 4;
        for(int i = 0; i < blades; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.scale(scale,scale,scale);
            switch (facing) {
                case DOWN:
                    GlStateManager.rotate(-90, 1, 0, 0);break;
                case UP:
                    GlStateManager.rotate(90, 1, 0, 0);break;
                case NORTH:
                    break;
                case WEST:
                    GlStateManager.rotate(90, 0, 1, 0);break;
                case SOUTH:
                    GlStateManager.rotate(180, 0, 1, 0);break;
                case EAST:
                    GlStateManager.rotate(270, 0, 1, 0);break;
            }
            //GlStateManager.rotate( rotationAngle + (360.0f * i) / blades, facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            GlStateManager.rotate( rotationAngle + (360.0f * i) / blades, 0, 0, 1);
            GlStateManager.translate(-0.5, -0.5, -0.5);

            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }

}
