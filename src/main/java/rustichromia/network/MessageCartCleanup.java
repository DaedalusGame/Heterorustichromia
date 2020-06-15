package rustichromia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustichromia.cart.CartDataClient;

import java.util.UUID;

public class MessageCartCleanup implements IMessage {
    UUID uuid;

    public MessageCartCleanup() {
    }

    public MessageCartCleanup(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        uuid = buffer.readUniqueId();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeUniqueId(uuid);
    }

    public static class MessageHolder implements IMessageHandler<MessageCartCleanup, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageCartCleanup message, final MessageContext ctx) {
            World world = Minecraft.getMinecraft().world;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                CartDataClient.remove(message.uuid);
            });
            return null;
        }
    }
}
