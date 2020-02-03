package rustichromia.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import rustichromia.Registry;
import rustichromia.util.Attributes;

import java.util.Random;

public class LayerSpear implements LayerRenderer<EntityLivingBase> {
    private final RenderLivingBase<?> renderer;

    public LayerSpear(RenderLivingBase<?> rendererIn)
    {
        this.renderer = rendererIn;
    }

    public static void initialize() {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        for (Render renderer : renderManager.entityRenderMap.values()) {
            if(renderer instanceof RenderLivingBase)
                ((RenderLivingBase) renderer).addLayer(new LayerSpear((RenderLivingBase<?>) renderer));
        }
        for(Render renderer : renderManager.getSkinMap().values()) {
            if(renderer instanceof RenderPlayer)
                ((RenderPlayer) renderer).addLayer(new LayerSpear((RenderPlayer) renderer));
        }
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        IAttributeInstance spears = entitylivingbaseIn.getEntityAttribute(Attributes.SPEARS);
        int i = (int) Math.floor(spears.getBaseValue());

        if (i > 0)
        {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
            IBakedModel spear = modelmanager.getModel(new ModelResourceLocation(Registry.SPEAR.getRegistryName(), "normal"));

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Random random = new Random((long)entitylivingbaseIn.getEntityId());
            //RenderHelper.disableStandardItemLighting();

            for (int j = 0; j < i; ++j)
            {
                GlStateManager.pushMatrix();
                ModelRenderer modelrenderer = this.renderer.getMainModel().getRandomModelBox(random);
                ModelBox modelbox = modelrenderer.cubeList.get(random.nextInt(modelrenderer.cubeList.size()));
                modelrenderer.postRender(0.0625F);
                float f = random.nextFloat();
                float f1 = random.nextFloat();
                float f2 = random.nextFloat();
                float f3 = (modelbox.posX1 + (modelbox.posX2 - modelbox.posX1) * f) / 16.0F;
                float f4 = (modelbox.posY1 + (modelbox.posY2 - modelbox.posY1) * f1) / 16.0F;
                float f5 = (modelbox.posZ1 + (modelbox.posZ2 - modelbox.posZ1) * f2) / 16.0F;
                GlStateManager.translate(f3, f4, f5);
                GlStateManager.rotate((float)(random.nextFloat()*360),1,0,0);
                GlStateManager.rotate((float)(random.nextFloat()*360),0,1,0);
                GlStateManager.rotate((float)(random.nextFloat()*360),0,0,1);
                GlStateManager.translate(-0.5,0,-0.5);
                blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(spear, 1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }

            //RenderHelper.enableStandardItemLighting();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
