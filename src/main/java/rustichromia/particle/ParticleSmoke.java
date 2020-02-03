package rustichromia.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

public class ParticleSmoke extends Particle implements ISpecialParticle {
    static Random random = new Random();

    public Color color;
    public float minScale;
    public float maxScale;
    public float initAlpha;
    public float partialTime;
    public ResourceLocation texture = new ResourceLocation("rustichromia:entity/particle_smoke");
    public ParticleSmoke(World worldIn, double x, double y, double z, double vx, double vy, double vz, Color color, float scaleMin, float scaleMax, int lifetime, float partialTime) {
        super(worldIn, x,y,z,0,0,0);
        this.color = color;
        this.setRBGColorF(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
        this.particleMaxAge = (int)((float)lifetime*0.5f);
        this.particleScale = scaleMin;
        this.minScale = scaleMin;
        this.maxScale = scaleMax;
        this.motionX = vx*2.0f;
        this.motionY = vy*2.0f;
        this.motionZ = vz*2.0f;
        this.canCollide = false;
        this.initAlpha = color.getAlpha()/255f;
        this.particleAngle = rand.nextFloat()*2.0f*(float)Math.PI;
        this.partialTime = partialTime;
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
        this.setParticleTexture(sprite);
    }

    @Override
    public int getBrightnessForRender(float pTicks){
        return super.getBrightnessForRender(pTicks);
    }

    @Override
    public boolean shouldDisableDepth(){
        return false;
    }

    @Override
    public int getFXLayer(){
        return 1;
    }

    @Override
    public void onUpdate(){
        super.onUpdate();
        float lifeCoeff = (particleAge + partialTime)/(float)particleMaxAge;
        float dScale = (maxScale - minScale) / 2;
        this.particleScale = minScale + dScale + dScale*(float)Math.sin(lifeCoeff*Math.PI);
        this.particleAlpha = (float)MathHelper.clampedLerp(initAlpha,0, lifeCoeff);
        this.prevParticleAngle = particleAngle;
        particleAngle += 1.0f;
    }

    @Override
    public boolean isAdditive() {
        return false;
    }

    @Override
    public boolean renderThroughBlocks() {
        return false;
    }
}
