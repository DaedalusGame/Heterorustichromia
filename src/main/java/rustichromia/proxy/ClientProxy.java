package rustichromia.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import rustichromia.tile.*;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmill.class, new TileEntityWindmillRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMechTorch.class, new TileEntityMechTorchRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRatiobox.class, new TileEntityRatioboxRenderer());
    }
}
