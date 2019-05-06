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
import rustichromia.block.BlockMechTorch;
import rustichromia.block.BlockWindmill;

public class TileEntityMechTorchRenderer extends TileEntitySpecialRenderer<TileEntityMechTorch> {
    @Override
    public void render(TileEntityMechTorch tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        BlockMechTorch block = (BlockMechTorch) tile.getBlockType();
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "dial"));

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        EnumFacing facing = tile.getFacing();

        double angle = tile.angle;
        double lastAngle = tile.lastAngle;
        float rotationAngle = (float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle;
        float offset = 0;

        GlStateManager.translate(0.5, 0.5, 0.5);
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(180, 1, 0, 0);
                offset = 180;
                break;
            case UP:
                break;
            case NORTH:
                GlStateManager.rotate(-90, 1, 0, 0);
                break;
            case SOUTH:
                GlStateManager.rotate(90, 1, 0, 0);
                offset = 180;
                break;
            case EAST:
                GlStateManager.rotate(90, 0, 0, 1);
                offset = 90;
                break;
            case WEST:
                GlStateManager.rotate(-90, 0, 0, 1);
                offset = -90;
                break;
        }
        //GlStateManager.rotate( rotationAngle, facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
        GlStateManager.rotate(rotationAngle + offset, 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);

        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.popMatrix();
    }

}
