package rustichromia.cart.control;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.cart.CartData;
import rustichromia.cart.Control;
import rustichromia.cart.ControlSupplier;
import rustichromia.tile.TileEntityCartControl;

public class StopEmptyTrash extends StopBase {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "stop_trash_empty");
    public static final ModelResourceLocation ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID, "cart_control/stop_trash_empty"), "inventory");
    public static final ControlSupplier SUPPLIER = new ControlSupplier(RESOURCE_LOCATION, "stop_trash_empty", ITEM_MODEL) {
        @Override
        public Control get() {
            return new StopEmptyTrash();
        }
    };

    public StopEmptyTrash() {
        super(RESOURCE_LOCATION);
    }

    @Override
    protected boolean shouldContinue(TileEntityCartControl tile, CartData cart) {
        return cart.getState().getTime() > 30 && !cart.content.isInUse();
    }

    @Override
    protected boolean shouldStop(TileEntityCartControl tile, CartData cart) {
        return true;
    }

    @Override
    protected void whileStopped(TileEntityCartControl tile, CartData cart) {
        if(cart.getState().getTime() % 10 == 0) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                cart.content.pushUnfiltered(facing);
            }
        }
    }

    @Override
    public ResourceLocation getTexture(TileEntityCartControl tile, EnumFacing facing) {
        return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/stop_trash_empty");
    }
}
