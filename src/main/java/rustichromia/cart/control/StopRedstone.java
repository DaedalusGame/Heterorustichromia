package rustichromia.cart.control;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.cart.CartData;
import rustichromia.cart.CartState;
import rustichromia.cart.Control;
import rustichromia.cart.ControlSupplier;
import rustichromia.tile.TileEntityCartControl;

import javax.annotation.Nonnull;

public class StopRedstone extends StopBase {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "stop_redstone");
    public static final ModelResourceLocation ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID, "cart_control/stop_redstone"), "inventory");
    public static final ControlSupplier SUPPLIER = new ControlSupplier(RESOURCE_LOCATION, "stop_redstone", ITEM_MODEL) {
        @Override
        public Control get() {
            return new StopRedstone();
        }
    };

    public StopRedstone() {
        super(RESOURCE_LOCATION);
    }

    @Override
    protected boolean shouldStop(TileEntityCartControl tile, CartData cart) {
        return tile.isPowered();
    }

    @Override
    protected boolean shouldContinue(TileEntityCartControl tile, CartData cart) {
        return !tile.isPowered();
    }

    @Override
    public ResourceLocation getTexture(TileEntityCartControl tile, EnumFacing facing) {
        if(tile.isPowered())
            return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/stop_redstone_on");
        else
            return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/stop_redstone_off");
    }
}
