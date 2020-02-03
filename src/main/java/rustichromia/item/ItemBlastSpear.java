package rustichromia.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustichromia.Rustichromia;
import rustichromia.entity.EntitySpear;
import rustichromia.network.MessageBlastDash;
import rustichromia.network.MessageEntitySwing;
import rustichromia.network.PacketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemBlastSpear extends Item {
    public ItemBlastSpear() {
        super();
        this.addPropertyOverride(new ResourceLocation(Rustichromia.MODID, "hand"), (stack, worldIn, entityIn) -> getHand(stack, entityIn));
        this.addPropertyOverride(new ResourceLocation(Rustichromia.MODID, "loaded"), (stack, worldIn, entityIn) -> getLoaded(stack, entityIn));
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean sneaking;
    public static HashMap<UUID, Float> cooldownTicksServer = new HashMap<>();

    public static void setCooldown(UUID uuid, float ticks) {
        cooldownTicksServer.put(uuid, ticks);
    }

    public static boolean hasCooldown(UUID uuid) {
        return cooldownTicksServer.getOrDefault(uuid, 0.0f) > 0;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (UUID uuid : cooldownTicksServer.keySet()) {
                Float ticks = cooldownTicksServer.get(uuid) - 1;
                cooldownTicksServer.put(uuid, ticks);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityLivingBase entity = Minecraft.getMinecraft().player;
        if (entity instanceof EntityPlayer) {
            World world = entity.world;
            if (world.isRemote && entity.isSneaking() && !sneaking) {
                ItemStack stack = entity.getHeldItemOffhand();
                if (stack.getItem() instanceof ItemBlastSpear && entity.onGround) {
                    double moveX = 0;
                    double moveZ = 0;
                    GameSettings settings = Minecraft.getMinecraft().gameSettings;
                    float fyaw = entity.rotationYaw * 0.017453292F;
                    if (settings.keyBindForward.isKeyDown()) {
                        moveX -= Math.sin(fyaw);
                        moveZ -= -Math.cos(fyaw);
                    }
                    if (settings.keyBindRight.isKeyDown()) {
                        moveX -= Math.sin(fyaw + Math.PI / 2);
                        moveZ -= -Math.cos(fyaw + Math.PI / 2);
                    }
                    if (settings.keyBindLeft.isKeyDown()) {
                        moveX -= Math.sin(fyaw - Math.PI / 2);
                        moveZ -= -Math.cos(fyaw - Math.PI / 2);
                    }
                    if (moveX != 0 || moveZ != 0)
                        PacketHandler.INSTANCE.sendToServer(new MessageBlastDash(moveX, moveZ));
                }
            }
            sneaking = entity.isSneaking();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (handIn == EnumHand.MAIN_HAND)
            return new ActionResult<>(EnumActionResult.PASS, stack);
        if (playerIn.getCooldownTracker().getCooldown(this, 0) > 0)
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        if (worldIn.isRemote && Rustichromia.PROXY.isThirdPerson())
            playerIn.swingArm(handIn);
        else
            PacketHandler.INSTANCE.sendToAllTracking(new MessageEntitySwing(playerIn, EnumHand.OFF_HAND), playerIn);
        if (!worldIn.isRemote) {

            EntitySpear spear = new EntitySpear(worldIn, playerIn);
            double handmod = -1.0;
            handmod *= playerIn.getPrimaryHand() == EnumHandSide.RIGHT ? 1.0 : -1.0;
            double posX = playerIn.posX + playerIn.getLookVec().x + handmod * (playerIn.width * 1.1) * Math.sin(Math.toRadians(-playerIn.rotationYaw - 90));
            double posY = playerIn.posY + playerIn.getEyeHeight() - 0.4 + playerIn.getLookVec().y;
            double posZ = playerIn.posZ + playerIn.getLookVec().z + handmod * (playerIn.width * 1.1) * Math.cos(Math.toRadians(-playerIn.rotationYaw - 90));
            spear.setPosition(posX, posY, posZ);
            spear.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 3.0F, 0.0F);
            worldIn.spawnEntity(spear);
            playerIn.getCooldownTracker().setCooldown(this, 30);
            setCooldown(playerIn.getUniqueID(), 10);
        }
        playerIn.motionX += Math.sin(playerIn.rotationYaw * 0.017453292F) * 0.3;
        playerIn.motionZ += -Math.cos(playerIn.rotationYaw * 0.017453292F) * 0.3;
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private float getLoaded(ItemStack stack, EntityLivingBase entityIn) {
        if (entityIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityIn;
            if (player.getCooldownTracker().getCooldown(this, 0) <= 0)
                return 1;
        }
        return 0;
    }

    private float getHand(ItemStack stack, EntityLivingBase entity) {
        if (entity != null) {
            if (entity.getHeldItemMainhand() == stack)
                return entity.getPrimaryHand() == EnumHandSide.RIGHT ? 0 : 1;
            else
                return entity.getPrimaryHand() == EnumHandSide.LEFT ? 0 : 1;
        }
        return 1;
    }
}
