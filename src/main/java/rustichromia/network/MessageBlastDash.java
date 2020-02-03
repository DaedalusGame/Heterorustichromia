package rustichromia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rustichromia.item.ItemBlastSpear;

import java.util.Random;
import java.util.UUID;

public class MessageBlastDash implements IMessage {
    public static Random random = new Random();
    double moveX = 0;
    double moveZ = 0;

    public MessageBlastDash() {
        super();
    }

    public MessageBlastDash(double moveX, double moveZ) {
        super();
        this.moveX = moveX;
        this.moveZ = moveZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        moveX = buf.readDouble();
        moveZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(moveX);
        buf.writeDouble(moveZ);
    }

    public static class MessageHolder implements IMessageHandler<MessageBlastDash, IMessage> {
        @Override
        public IMessage onMessage(final MessageBlastDash message, final MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            WorldServer world = ctx.getServerHandler().player.getServerWorld();
            world.addScheduledTask(() -> {
                ItemStack heldStack = player.getHeldItemOffhand();
                UUID uuid = player.getUniqueID();
                if (heldStack.getItem() instanceof ItemBlastSpear && !ItemBlastSpear.hasCooldown(uuid)) {
                    player.velocityChanged = true;
                    double lookLen = Math.sqrt(message.moveX * message.moveX + message.moveZ * message.moveZ);
                    if(lookLen != 0) {
                        double dashStrength = 10;
                        player.motionX += message.moveX * dashStrength / lookLen;
                        player.motionZ += message.moveZ * dashStrength / lookLen;
                    }
                    //player.getCooldownTracker().setCooldown(heldStack.getItem(), 10);
                    ItemBlastSpear.setCooldown(uuid, 10);
                }
            });
            return null;
        }
    }

}