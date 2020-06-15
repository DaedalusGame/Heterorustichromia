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

public class Trigger extends Control {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "trigger");
    public static final ModelResourceLocation ITEM_MODEL = new ModelResourceLocation(new ResourceLocation(Rustichromia.MODID, "cart_control/trigger"), "inventory");
    public static final ControlSupplier SUPPLIER = new ControlSupplier(RESOURCE_LOCATION, "trigger", ITEM_MODEL) {
        @Override
        public Control get() {
            return new Trigger();
        }
    };

    EnumFacing facing;
    int count;

    public Trigger() {
        super(RESOURCE_LOCATION);
    }

    @Override
    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public Control setFacing(EnumFacing facing) {
        switch (facing) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                this.facing = facing;
                return this;
            default:
                throw new IllegalArgumentException("Control orientation can't be " + facing);
        }
    }

    @Override
    public ResourceLocation getTexture(TileEntityCartControl tile, EnumFacing facing) {
        if(count % 2 == 0)
            return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/trigger0");
        else
            return new ResourceLocation(Rustichromia.MODID,"blocks/cart_control/trigger1");
    }

    @Override
    public boolean isActiveSide(TileEntityCartControl tile, EnumFacing facing) {
        EnumFacing forward = getTrueFacing(tile.getFacing());
        return forward.getAxis() != facing.getAxis();
    }

    @Override
    public void controlCart(TileEntityCartControl tile, CartData cart) {
        EnumFacing forward = getTrueFacing(tile.getFacing());
        EnumFacing backward = getTrueFacing(tile.getFacing()).getOpposite();

        EnumFacing direction = cart.getForward();

        if(direction == forward || direction == backward)
            cart.moveForward();
        else if(cart.turnMachine.canChange() && cart.moveMachine.canChange()) {
            cart.orient(count % 2 == 0 ? forward : backward);
            count++;
            tile.markDirty();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        nbt.setString("facing", getFacing().getName());
        nbt.setInteger("count", count);
        return nbt;
    }

    @Override
    public Control readFromNBT(@Nonnull NBTTagCompound nbt) {
        EnumFacing facing = EnumFacing.byName(nbt.getString("facing"));
        switch (facing) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                this.facing = facing;
                break;
            default:
                this.facing = EnumFacing.NORTH;
                break;
        }
        count = nbt.getInteger("count");
        return this;
    }
}
