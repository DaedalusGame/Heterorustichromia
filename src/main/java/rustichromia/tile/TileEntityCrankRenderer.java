package rustichromia.tile;

import mysticalmechanics.api.IHasRotation;
import mysticalmechanics.block.BlockAxle;
import mysticalmechanics.tileentity.TileEntityAxle;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import rustichromia.block.BlockCrank;

public class TileEntityCrankRenderer extends TileEntitySpecialRenderer<TileEntityCrank> {
    public TileEntityCrankRenderer(){
        super();
    }

    @Override
    public void render(TileEntityCrank tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        Block block = state.getBlock();
        if (block instanceof BlockCrank){
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "normal"));

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            EnumFacing facing = tile.getFacing();

            float rotationDirection = 1;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x+0.5, y+0.5, z+0.5);
            switch(facing)
            {

                case DOWN:
                    GlStateManager.rotate(180, 1, 0, 0);
                    break;
                case UP:
                    rotationDirection = -1;
                    break;
                case NORTH:
                    rotationDirection = -1;
                    GlStateManager.rotate(-90, 1, 0, 0);
                    break;
                case SOUTH:
                    GlStateManager.rotate(90, 1, 0, 0);
                    break;
                case WEST:
                    rotationDirection = -1;
                    GlStateManager.rotate(-90, 0, 1, 0);
                    GlStateManager.rotate(90, 1, 0, 0);
                    break;
                case EAST:
                    GlStateManager.rotate(90, 0, 1, 0);
                    GlStateManager.rotate(90, 1, 0, 0);
                    break;
            }

            syncAngle(tile, facing.getOpposite());

            double angle = tile.angle;
            double lastAngle = tile.lastAngle;

            float rotation = (float) (partialTicks * angle) + (1 - partialTicks) * (float) lastAngle;
            GlStateManager.rotate(rotationDirection * rotation, 0, 1, 0);
            GlStateManager.translate(-0.5, -0.5, -0.5);

            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);

            GlStateManager.popMatrix();
        }

    }

    private void syncAngle(TileEntityCrank tile, EnumFacing checkDirection) {
        BlockPos axlePos = tile.getPos().offset(checkDirection);
        TileEntity axleTile = tile.getWorld().getTileEntity(axlePos);
        if(axleTile instanceof IHasRotation) {
            IHasRotation axle = (IHasRotation) axleTile;
            if(axle.hasRotation(checkDirection.getOpposite())) {
                tile.angle = axle.getAngle(checkDirection.getOpposite());
                tile.lastAngle = axle.getLastAngle(checkDirection.getOpposite());
            }
        }
    }
}

