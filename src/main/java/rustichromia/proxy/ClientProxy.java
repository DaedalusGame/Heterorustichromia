package rustichromia.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import rustichromia.tile.TileEntityMechTorch;
import rustichromia.tile.TileEntityMechTorchRenderer;
import rustichromia.tile.TileEntityWindmill;
import rustichromia.tile.TileEntityWindmillRenderer;

public class ClientProxy implements IProxy {
    @Override
    public void preInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmill.class, new TileEntityWindmillRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMechTorch.class, new TileEntityMechTorchRenderer());
    }
}
