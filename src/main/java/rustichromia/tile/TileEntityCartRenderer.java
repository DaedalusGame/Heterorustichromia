package rustichromia.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import rustichromia.block.BlockCart;
import rustichromia.util.Rotation;

public class TileEntityCartRenderer extends TileEntitySpecialRenderer<TileEntityCart> {
    @Override
    public void render(TileEntityCart tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        /*IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        Block block = state.getBlock();
        if (block instanceof BlockCart) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "normal"));

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            Vec3d offset = new Vec3d(
                    MathHelper.clampedLerp(tile.lastOffset.x,tile.offset.x,partialTicks) - tile.getPos().getX(),
                    MathHelper.clampedLerp(tile.lastOffset.y,tile.offset.y,partialTicks) - tile.getPos().getY(),
                    MathHelper.clampedLerp(tile.lastOffset.z,tile.offset.z,partialTicks) - tile.getPos().getZ()
            );

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + offset.x, y + offset.y, z + offset.z);
            GlStateManager.translate(0.5, 0.5, 0.5);
            Rotation rotation = Rotation.lerp(tile.lastRotation,tile.rotation,MathHelper.clamp(partialTicks,0,1));
            rotation.GLrotate();
            GlStateManager.translate(-0.5, -0.5, -0.5);

            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }*/
    }
}
