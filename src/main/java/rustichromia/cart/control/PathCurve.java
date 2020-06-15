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
import rustichromia.util.Misc;

import javax.annotation.Nonnull;

public class PathCurve extends Control {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "path_curve");
    public static final ModelResourceLocation ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID, "cart_control/path_curve"), "inventory");
    public static final ControlSupplier SUPPLIER = new ControlSupplier(RESOURCE_LOCATION, "path_curve", ITEM_MODEL) {
        @Override
        public Control get() {
            return NORTH;
        }
    };

    public static final PathCurve NORTH = new PathCurve(EnumFacing.NORTH);
    public static final PathCurve SOUTH = new PathCurve(EnumFacing.SOUTH);
    public static final PathCurve EAST = new PathCurve(EnumFacing.EAST);
    public static final PathCurve WEST = new PathCurve(EnumFacing.WEST);

    EnumFacing facing;

    public PathCurve(EnumFacing facing) {
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
        if(facing == tile.getFacing())
            return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/path_curve_mirror");
        else
            return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/path_curve");
    }

    @Override
    public boolean isActiveSide(TileEntityCartControl tile, EnumFacing facing) {
        EnumFacing up = tile.getFacing();
        return up.getAxis() == facing.getAxis();
    }

    @Override
    public void controlCart(TileEntityCartControl tile, CartData cart) {
        EnumFacing direction = cart.getForward();
        int horizontalInversion = tile.getFacing().getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1 : -1;

        EnumFacing forward = getTrueFacing(tile.getFacing());
        EnumFacing right = Misc.turn(forward, tile.getFacing().getAxis(), -1 * horizontalInversion);

        if(direction == forward || direction == right)
            cart.moveForward();
        if(direction == forward.getOpposite())
            cart.orient(right);
        if(direction == right.getOpposite())
            cart.orient(forward);
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
