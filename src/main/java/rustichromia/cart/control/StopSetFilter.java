package rustichromia.cart.control;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.cart.CartData;
import rustichromia.cart.Control;
import rustichromia.cart.ControlSupplier;
import rustichromia.tile.TileEntityCartControl;
import rustichromia.tile.TileEntityFilterBase;

public class StopSetFilter extends StopBase {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "stop_filter");
    public static final ModelResourceLocation ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID, "cart_control/stop_filter"), "inventory");
    public static final ControlSupplier SUPPLIER = new ControlSupplier(RESOURCE_LOCATION, "stop_filter", ITEM_MODEL) {
        @Override
        public Control get() {
            return new StopSetFilter();
        }
    };

    public StopSetFilter() {
        super(RESOURCE_LOCATION);
    }

    @Override
    protected boolean shouldContinue(TileEntityCartControl tile, CartData cart) {
        return cart.getState().getTime() > 30;
    }

    @Override
    protected boolean shouldStop(TileEntityCartControl tile, CartData cart) {
        return true;
    }

    @Override
    protected void whileStopped(TileEntityCartControl tile, CartData cart) {
        TileEntity tileCart = cart.getTile();
        if(cart.getState().getTime() % 10 == 0) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tileFilter = tileCart.getWorld().getTileEntity(tileCart.getPos().offset(facing));
                if(tileFilter instanceof TileEntityFilterBase)
                    ((TileEntityFilterBase) tileFilter).setFilter(cart);
            }
        }
    }

    @Override
    public ResourceLocation getTexture(TileEntityCartControl tile, EnumFacing facing) {
        return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/stop_filter");
    }
}
