package rustichromia.tile;

import mysticalmechanics.api.IHasRotation;
import mysticalmechanics.tileentity.TileEntityAxle;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TileEntityRatioboxRenderer extends TileEntitySpecialRenderer<TileEntityRatiobox> {
    public TileEntityRatioboxRenderer(){
        super();
    }

    @Override
    public void render(TileEntityRatiobox tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha){
        if (tile != null){
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            Block block = state.getBlock();

            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            BlockModelRenderer renderer = blockrendererdispatcher.getBlockModelRenderer();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            IBakedModel axle_input = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "axle_input"));
            IBakedModel axle_output_a = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "axle_output_a"));
            IBakedModel axle_output_b = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "axle_output_b"));

            syncAngle(tile.getPos(),tile.getInput(),(angle,lastAngle) -> {
                tile.inputAngle = angle;
                tile.inputLastAngle = lastAngle;
            });
            renderAxle(tile.getInput(), (float)MathHelper.clampedLerp(tile.inputLastAngle,tile.inputAngle,partialTicks), axle_input, renderer);
            if(tile.getSideA() != null) {
                syncAngle(tile.getPos(),tile.getSideA(),(angle,lastAngle) -> {
                    tile.aAngle = angle;
                    tile.aLastAngle = lastAngle;
                });
                renderAxle(tile.getSideA(), (float) MathHelper.clampedLerp(tile.aLastAngle, tile.aAngle, partialTicks), axle_output_a, renderer);
            }
            if(tile.getSideB() != null) {
                syncAngle(tile.getPos(),tile.getSideB(),(angle,lastAngle) -> {
                    tile.bAngle = angle;
                    tile.bLastAngle = lastAngle;
                });
                renderAxle(tile.getSideB(), (float) MathHelper.clampedLerp(tile.bLastAngle, tile.bAngle, partialTicks), axle_output_b, renderer);
            }
            GlStateManager.popMatrix();
        }
    }

    private void syncAngle(BlockPos pos, EnumFacing direction, BiConsumer<Double, Double> consumer) {
        BlockPos checkPos = pos.offset(direction);
        TileEntity axleTile = getWorld().getTileEntity(checkPos);
        if(axleTile instanceof IHasRotation) { //I hate it
            IHasRotation axle = (IHasRotation) axleTile;
            if(axle.hasRotation(direction.getOpposite())) {
                consumer.accept(axle.getAngle(direction.getOpposite()),axle.getLastAngle(direction.getOpposite()));
            }
        }
    }

    private void renderAxle(EnumFacing side, float angle, IBakedModel model, BlockModelRenderer renderer) {


        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        switch (side) {
            case DOWN:
                GlStateManager.rotate(180, 1, 0, 0);
                break;
            case UP:
                GlStateManager.rotate(0, 1, 0, 0);
                angle = -angle;
                break;
            case NORTH:
                GlStateManager.rotate(-90, 1, 0, 0);
                angle = -angle;
                break;
            case SOUTH:
                GlStateManager.rotate(90, 1, 0, 0);
                break;
            case WEST:
                GlStateManager.rotate(-90, 0, 1, 0);
                GlStateManager.rotate(90, 1, 0, 0);
                angle = -angle;
                break;
            case EAST:
                GlStateManager.rotate(90, 0, 1, 0);
                GlStateManager.rotate(90, 1, 0, 0);
                break;
        }
        GlStateManager.rotate(angle, 0, 1, 0);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        renderer.renderModelBrightnessColor(model, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
