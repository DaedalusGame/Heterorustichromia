package rustichromia.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import rustichromia.cart.Control;

import java.awt.*;

public class TileEntityCartControlRenderer extends TileEntitySpecialRenderer<TileEntityCartControl> {

    @Override
    public void render(TileEntityCartControl tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, tileAlpha);

        if(tile.control != null) {
            Control control = tile.getControl();
            Color color = Color.WHITE;

            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();

            EnumFacing side = tile.getFacing();
            EnumFacing forward = tile.getTrueFacing();

            for (EnumFacing facing : EnumFacing.VALUES) {
                TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(control.getTexture(tile, facing).toString());
                if(!control.isActiveSide(tile, facing))
                    continue;
                if(side == facing.getOpposite())
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                else
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                renderControl(tile, facing, x, y, z, sprite, color, -0.01);
            }
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            //renderControl(tile, forward, x, y, z, sprite, color, 1.01);

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        }
    }

    private void renderControl(TileEntityCartControl tile, EnumFacing facing, double x, double y, double z, TextureAtlasSprite sprite, Color color, double offsetY) {
        int c = color.getRGB();
        int blue = c & 0xFF;
        int green = (c >> 8) & 0xFF;
        int red = (c >> 16) & 0xFF;
        int alpha = (c >> 24) & 0xFF;

        double minU = sprite.getMinU();
        double maxU = sprite.getMaxU();
        double minV = sprite.getMinV();
        double maxV = sprite.getMaxV();

        int i = getWorld().getCombinedLight(tile.getPos(), 0);
        int lightx = i >> 0x10 & 0xFFFF;
        int lighty = i & 0xFFFF;

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tess = Tessellator.getInstance();

        EnumFacing side = tile.getFacing();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.translate(-side.getFrontOffsetX(), -side.getFrontOffsetY(), -side.getFrontOffsetZ());
        GlStateManager.translate(0.5, 0.5, 0.5);
        GlStateManager.rotate(tile.getRotation(facing));
        GlStateManager.translate(-0.5, -0.5, -0.5);
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        buffer.pos(0, offsetY, 0).tex(minU, minV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
        buffer.pos(1, offsetY, 0).tex(maxU, minV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
        buffer.pos(1, offsetY, 1).tex(maxU, maxV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
        buffer.pos(0, offsetY, 1).tex(minU, maxV).lightmap(lightx, lighty).color(red, green, blue, alpha).endVertex();
        tess.draw();
        GlStateManager.popMatrix();
    }
}
