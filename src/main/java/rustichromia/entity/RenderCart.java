package rustichromia.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.util.vector.Quaternion;
import rustichromia.Registry;
import rustichromia.block.BlockCart;
import rustichromia.cart.CartDataClient;
import rustichromia.util.Misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderCart extends Render<EntityCart> {
    protected RenderCart(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityCart entity) {
        return null;
    }

    @Override
    public void doRender(@Nonnull EntityCart entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        CartDataClient data = entity.getData();
        Block block = Registry.CART;
        if (block instanceof BlockCart) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            IBakedModel ibakedmodel = modelmanager.getModel(new ModelResourceLocation(block.getRegistryName(), "normal"));

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            Vec3d offset = new Vec3d(
                    MathHelper.clampedLerp(data.lastOffset.x,data.offset.x,partialTicks) - entity.posX,
                    MathHelper.clampedLerp(data.lastOffset.y,data.offset.y,partialTicks) - entity.posY,
                    MathHelper.clampedLerp(data.lastOffset.z,data.offset.z,partialTicks) - entity.posZ
            );

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + offset.x, y + offset.y, z + offset.z);
            GlStateManager.translate(0.5, 0.5, 0.5);
            Quaternion rotation = Misc.slerp(data.lastRotation, data.rotation, MathHelper.clamp(partialTicks, 0, 1));
            GlStateManager.rotate(rotation);
            GlStateManager.translate(-0.5, -0.5, -0.5);

            blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    public static class Factory implements IRenderFactory<EntityCart> {

        @Override
        public Render<? super EntityCart> createRenderFor(RenderManager manager) {
            return new RenderCart(manager);
        }

    }
}
