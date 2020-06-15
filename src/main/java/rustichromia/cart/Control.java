package rustichromia.cart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import rustichromia.tile.TileEntityCartControl;
import rustichromia.util.Misc;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public abstract class Control {
    private static final HashMap<ResourceLocation, ControlSupplier> suppliers = new LinkedHashMap<>();

    public static void register(ControlSupplier supplier) {
        suppliers.put(supplier.getResourceLocation(), supplier);
    }

    public static Control deserialize(NBTTagCompound compound) {
        ResourceLocation resLoc = new ResourceLocation(compound.getString("type"));
        try {
            Supplier<Control> supplier = suppliers.get(resLoc);
            Control result = supplier.get().readFromNBT(compound);
            return result;
        } catch (Exception e) {
            System.out.println("Failed to deserialize cart control '"+resLoc+"'");
        }
        return null;
    }

    public static ControlSupplier get(ResourceLocation resourceLocation) {
        return suppliers.get(resourceLocation);
    }

    public static Collection<ControlSupplier> getSuppliers() {
        return suppliers.values();
    }

    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("type", resourceLocation.toString());
        writeToNBT(nbt);
        return nbt;
    }

    private ResourceLocation resourceLocation;

    public Control(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public abstract EnumFacing getFacing();

    public abstract Control setFacing(EnumFacing facing);

    public abstract ResourceLocation getTexture(TileEntityCartControl tile, EnumFacing facing);

    public abstract boolean isActiveSide(TileEntityCartControl tile, EnumFacing facing);

    public abstract void controlCart(TileEntityCartControl tile, CartData cart);

    public EnumFacing getTrueFacing(EnumFacing base) {
        EnumFacing orientation = getFacing();
        return Misc.getTrueFacing(orientation, base);
    }

    protected void moveRail(CartData cart) {
        if(cart.hasControlAhead()) {
            cart.moveForward();
        }
    }

    public abstract NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt);

    public abstract Control readFromNBT(@Nonnull NBTTagCompound nbt);
}
