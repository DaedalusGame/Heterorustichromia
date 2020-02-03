package rustichromia.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import rustichromia.Registry;

import javax.annotation.Nullable;

public class RenderSpear extends Render<EntitySpear> {
    protected RenderSpear(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySpear entity) {
        return null;
    }

    @Override
    public void doRender(EntitySpear entity, double x, double y, double z, float entityYaw, float partialTicks) {
        /*GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel spear = modelmanager.getModel(new ModelResourceLocation(Registry.SPEAR.getRegistryName(), "normal"));



        GlStateManager.translate(0.5f,0.5f,0.5f);
        GlStateManager.rotate(yaw, 0, 1, 0);
		GlStateManager.rotate(90-pitch, 1, 0, 0);
        GlStateManager.translate(-0.5f,-0.5f,-0.5f);
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(spear, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();*/
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static class Factory implements IRenderFactory<EntitySpear> {

        @Override
        public Render<? super EntitySpear> createRenderFor(RenderManager manager) {
            return new RenderSpear(manager);
        }

    }
}
