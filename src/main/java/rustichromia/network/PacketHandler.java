package rustichromia.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import rustichromia.Rustichromia;

public class PacketHandler {
    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Rustichromia.MODID);

    private static int id = 0;

    public static void registerMessages(){
        INSTANCE.registerMessage(MessageBlastDash.MessageHolder.class,MessageBlastDash.class,id ++,Side.SERVER);
        INSTANCE.registerMessage(MessageEntitySwing.MessageHolder.class,MessageEntitySwing.class,id ++,Side.CLIENT);
        INSTANCE.registerMessage(MessageSelectAssemblerRecipe.MessageHolder.class,MessageSelectAssemblerRecipe.class,id ++,Side.SERVER);
        INSTANCE.registerMessage(MessageUpdateRatiobox.MessageHolder.class,MessageUpdateRatiobox.class,id ++,Side.SERVER);
        INSTANCE.registerMessage(MessageCartCleanup.MessageHolder.class,MessageCartCleanup.class,id ++,Side.CLIENT);

    }
}
