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

public abstract class StopBase extends Control {
    EnumFacing facing;

    public StopBase(ResourceLocation resourceLocation) {
        super(resourceLocation);
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
    public boolean isActiveSide(TileEntityCartControl tile, EnumFacing facing) {
        EnumFacing forward = getTrueFacing(tile.getFacing());
        return forward.getAxis() != facing.getAxis();
    }

    @Override
    public void controlCart(TileEntityCartControl tile, CartData cart) {
        EnumFacing forward = getTrueFacing(tile.getFacing());

        EnumFacing direction = cart.getForward();

        //Incoming * -> STOPPING
        //Still STOPPING -> STOP
        //After n ticks STOP -> MOVE
        if (shouldStop(tile,cart)) {
            if (cart.isIncoming()) {
                cart.setState(CartState.STOPPING);
            } else if (cart.isState(CartState.STOPPING)) {
                if (cart.isStill())
                    cart.setState(CartState.STOP);
            } else if (cart.isState(CartState.STOP)) {
                whileStopped(tile, cart);
                if (shouldContinue(tile, cart))
                    cart.setState(CartState.MOVE);
            } else {
                cart.orientAndMove(forward);
            }
        } else {
            if (cart.isState(CartState.STOPPING) || cart.isState(CartState.STOP))
                cart.setState(CartState.MOVE);
            cart.orientAndMove(forward);
        }

        if (cart.turnMachine.canChange()) {
            cart.orient(forward);
        }
    }

    protected void whileStopped(TileEntityCartControl tile, CartData cart) {
        //NOOP
    }

    protected abstract boolean shouldContinue(TileEntityCartControl tile, CartData cart);

    protected abstract boolean shouldStop(TileEntityCartControl tile, CartData cart);

    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        nbt.setString("facing", getFacing().getName());
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
        return this;
    }
}
