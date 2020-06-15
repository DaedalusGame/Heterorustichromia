package rustichromia.cart.control;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;
import rustichromia.cart.CartData;
import rustichromia.cart.Control;
import rustichromia.cart.ControlSupplier;
import rustichromia.tile.TileEntityCartControl;

import javax.annotation.Nonnull;

public class Arrow extends Control {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "arrow");
    public static final ModelResourceLocation ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID, "cart_control/arrow"), "inventory");
    public static final ControlSupplier SUPPLIER = new ControlSupplier(RESOURCE_LOCATION, "arrow", ITEM_MODEL) {
        @Override
        public Control get() {
            return NORTH;
        }
    };

    public static final Arrow NORTH = new Arrow(EnumFacing.NORTH);
    public static final Arrow SOUTH = new Arrow(EnumFacing.SOUTH);
    public static final Arrow EAST = new Arrow(EnumFacing.EAST);
    public static final Arrow WEST = new Arrow(EnumFacing.WEST);

    EnumFacing facing;

    public Arrow(EnumFacing facing) {
        super(RESOURCE_LOCATION);
        this.facing = facing;
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public Control setFacing(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
            default:
                throw new IllegalArgumentException("Control orientation can't be " + facing);
        }
    }

    @Override
    public ResourceLocation getTexture(TileEntityCartControl tile, EnumFacing facing) {
        return new ResourceLocation(Rustichromia.MODID, "blocks/cart_control/arrow");
    }

    @Override
    public boolean isActiveSide(TileEntityCartControl tile, EnumFacing facing) {
        EnumFacing forward = getTrueFacing(tile.getFacing());
        return forward.getAxis() != facing.getAxis();
    }

    @Override
    public void controlCart(TileEntityCartControl tile, CartData cart) {
        EnumFacing forward = getTrueFacing(tile.getFacing());
        cart.orientAndMove(forward);
    }

    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        nbt.setString("facing", getFacing().getName());
        return nbt;
    }

    @Override
    public Control readFromNBT(@Nonnull NBTTagCompound nbt) {
        switch (EnumFacing.byName(nbt.getString("facing"))) {
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
            default:
                return null;
        }
    }
}
