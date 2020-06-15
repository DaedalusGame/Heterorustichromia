package rustichromia.cart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import rustichromia.tile.TileEntityCart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

public abstract class CartContent {
    private static final HashMap<ResourceLocation, CartContentSupplier> suppliers = new LinkedHashMap<>();

    public static void register(CartContentSupplier supplier) {
        suppliers.put(supplier.getResourceLocation(), supplier);
    }

    public static CartContent deserialize(NBTTagCompound compound) {
        ResourceLocation resLoc = new ResourceLocation(compound.getString("type"));
        try {
            Supplier<CartContent> supplier = suppliers.get(resLoc);
            CartContent result = supplier.get().readFromNBT(compound);
            return result;
        } catch (Exception e) {
            System.out.println("Failed to deserialize cart content '"+resLoc+"'");
        }
        return null;
    }

    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("type", resourceLocation.toString());
        writeToNBT(nbt);
        return nbt;
    }

    private ResourceLocation resourceLocation;
    protected CartData cart;
    protected int useTime;

    public CartContent(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public void setCart(CartData cart) {
        this.cart = cart;
    }

    //Used to check if the cart can be safely changed to another cargo type
    public abstract boolean isEmpty();

    //Used to check if the cart can move on from a stop
    public boolean isInUse() {
        return useTime > 0;
    }

    public void setInUse(int time) {
        useTime = time;
    }

    public void update() {
        if(useTime > 0)
            useTime--;
    }

    public abstract void pullFiltered(EnumFacing facing);

    public abstract void pullUnfiltered(EnumFacing facing);

    public abstract void pushFiltered(EnumFacing facing);

    public abstract void pushUnfiltered(EnumFacing facing);

    public abstract boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing);

    public abstract <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing);

    public abstract NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt);

    public abstract CartContent readFromNBT(@Nonnull NBTTagCompound nbt);
}
