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

public class MessageEntitySwing implements IMessage {
    Integer entityID;
    EnumHand hand;

    public MessageEntitySwing() {
    }

    public MessageEntitySwing(Entity entity, EnumHand hand) {
        this.entityID = entity.getEntityId();
        this.hand = hand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        entityID = buffer.readInt();
        hand = buffer.readEnumValue(EnumHand.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeInt(entityID);
        buffer.writeEnumValue(hand);
    }

    public static class MessageHolder implements IMessageHandler<MessageEntitySwing, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageEntitySwing message, final MessageContext ctx) {
            World world = Minecraft.getMinecraft().world;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = world.getEntityByID(message.entityID);
                if(Minecraft.getMinecraft().player != entity && entity instanceof EntityLivingBase)
                    ((EntityLivingBase) entity).swingArm(message.hand);
            });
            return null;
        }
    }
}
