package rustichromia.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rustichromia.gui.ContainerAssembler;

public class MessageSelectAssemblerRecipe implements IMessage {
    ResourceLocation recipe;
    boolean shouldClose;

    public MessageSelectAssemblerRecipe() {
        super();
    }

    public MessageSelectAssemblerRecipe(ResourceLocation recipe, boolean shouldClose) {
        super();
        this.recipe = recipe;
        this.shouldClose = shouldClose;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer pktBuf = new PacketBuffer(buf);
        boolean hasRecipe = pktBuf.readBoolean();
        if(hasRecipe)
            recipe = pktBuf.readResourceLocation();
        shouldClose = pktBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer pktBuf = new PacketBuffer(buf);
        pktBuf.writeBoolean(recipe != null);
        if(recipe != null)
            pktBuf.writeResourceLocation(recipe);
        pktBuf.writeBoolean(shouldClose);
    }

    public static class MessageHolder implements IMessageHandler<MessageSelectAssemblerRecipe, IMessage> {
        @Override
        public IMessage onMessage(final MessageSelectAssemblerRecipe message, final MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            WorldServer world = ctx.getServerHandler().player.getServerWorld();
            world.addScheduledTask(() -> {
                Container container = player.openContainer;
                if (container instanceof ContainerAssembler) {
                    ((ContainerAssembler) container).setRecipe(message.recipe, message.shouldClose);
                }
            });
            return null;
        }
    }
}
