package rustichromia.tile;

import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class TileEntityHopperWoodRenderer extends TileEntitySpecialRenderer<TileEntityHopperWood> {
    @Override
    public void render(TileEntityHopperWood tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        Block block = state.getBlock();

        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        BlockModelRenderer renderer = blockrendererdispatcher.getBlockModelRenderer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        IBakedModel cog = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "cog"));

        double rotation = MathHelper.clampedLerp(tile.lastAngle, tile.angle, partialTicks);

        for (EnumFacing facing : EnumFacing.VALUES) {
            if (facing == tile.getInputFacing() || facing == tile.getOutputFacing())
                continue;
            TileEntity connected = tile.getWorld().getTileEntity(tile.getPos().offset(facing));
            if (connected != null && connected.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()))
                renderAxle(facing, (float) rotation, cog, renderer);
        }

        GlStateManager.popMatrix();
    }

    private void renderAxle(EnumFacing side, float angle, IBakedModel model, BlockModelRenderer renderer) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        switch (side) {
            case DOWN:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                angle = -angle;
                break;
            case NORTH:
                //GlStateManager.rotate(-90, 1, 0, 0);
                angle = -angle;
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                angle = -angle;
                break;
            case EAST:
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                break;
        }
        GlStateManager.rotate(-angle, 0, 0, 1);
        GlStateManager.scale(0.7, 0.7, 1.0);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        GlStateManager.translate(0.0, 0.0, -0.5 + 0.03125);
        renderer.renderModelBrightnessColor(model, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
