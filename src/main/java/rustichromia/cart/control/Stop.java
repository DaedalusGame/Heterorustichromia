package rustichromia.cart.control;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.cart.CartData;
import rustichromia.cart.Control;
import rustichromia.cart.ControlSupplier;
import rustichromia.tile.TileEntityCartControl;

public class Stop extends StopBase {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "stop");
    public static final ModelResourceLocation ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID, "cart_control/stop"), "inventory");
    public static final ControlSupplier SUPPLIER = new ControlSupplier(RESOURCE_LOCATION, "stop", ITEM_MODEL) {
        @Override
        public Control get() {
            return new Stop();
        }
    };

    public Stop() {
        super(RESOURCE_LOCATION);
    }

    @Override
    protected boolean shouldStop(TileEntityCartControl tile, CartData cart) {
        return true;
    }

    @Override
    protected boolean shouldContinue(TileEntityCartControl tile, CartData cart) {
        return cart.getState().getTime() > 60 && !cart.content.isInUse();
    }

    @Override
    public ResourceLocation getTexture(TileEntityCartControl tile, EnumFacing facing) {
        return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/stop");
    }
}
