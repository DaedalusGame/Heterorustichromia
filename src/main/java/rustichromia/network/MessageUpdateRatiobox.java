package rustichromia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rustichromia.gui.ContainerRatioBox;

public class MessageUpdateRatiobox implements IMessage {
    double ratioOn;
    double ratioOff;

    public MessageUpdateRatiobox() {
        super();
    }

    public MessageUpdateRatiobox(double ratioOn, double ratioOff) {
        this.ratioOn = ratioOn;
        this.ratioOff = ratioOff;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ratioOn = buf.readDouble();
        ratioOff = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(ratioOn);
        buf.writeDouble(ratioOff);
    }

    public static class MessageHolder implements IMessageHandler<MessageUpdateRatiobox, IMessage> {
        @Override
        public IMessage onMessage(final MessageUpdateRatiobox message, final MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            WorldServer world = ctx.getServerHandler().player.getServerWorld();
            world.addScheduledTask(() -> {
                Container container = player.openContainer;
                if (container instanceof ContainerRatioBox) {
                    ((ContainerRatioBox) container).setRatio(message.ratioOn,message.ratioOff);
                }
            });
            return null;
        }
    }
}
