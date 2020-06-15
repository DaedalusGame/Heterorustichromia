package rustichromia.cart.content;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import rustichromia.Rustichromia;
import rustichromia.cart.CartContent;
import rustichromia.cart.CartContentSupplier;
import rustichromia.tile.TileEntityCart;
import rustichromia.util.ItemStackHandlerFiltered;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContentItems extends CartContent {
    public enum FilterType {
        ANY,
        FILTERED,
        UNFILTERED,
    }

    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "items");
    public static final CartContentSupplier SUPPLIER = new CartContentSupplier(RESOURCE_LOCATION) {
        @Override
        public CartContent get() {
            return new ContentItems();
        }
    };
    public static final int SLOTS = 9;

    protected ItemStackHandlerFiltered handler;

    public ContentItems() {
        this(0);
    }

    public ContentItems(int slots) {
        super(RESOURCE_LOCATION);
        this.handler = new ItemStackHandlerFiltered(slots) {
            @Override
            public void onContentsChanged() {
                setInUse(30);
            }
        };
    }

    public ItemStackHandlerFiltered getItemHandler() {
        return handler;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < handler.getSlots(); i++)
            if (!handler.getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    public IItemHandler getInventory(TileEntityCart tile, EnumFacing facing) {
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();

        BlockPos posInventory = pos.offset(facing);
        TileEntity tileInventory = world.getTileEntity(posInventory);
        if(tileInventory != null)
            return tileInventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
        return null;
    }

    private void moveItems(IItemHandler source, IItemHandler destination, int toMove, boolean filtered) {
        for (int extractSlot = 0; extractSlot < source.getSlots() && toMove > 0; extractSlot++) {
            if(filtered != isSlotFiltered(source, extractSlot))
                continue;
            ItemStack extracted = source.extractItem(extractSlot, toMove, true);
            int startCount = extracted.getCount();
            for (int insertSlot = 0; insertSlot < destination.getSlots() && !extracted.isEmpty(); insertSlot++) {
                if(filtered != isSlotFiltered(destination, insertSlot))
                    continue;
                extracted = destination.insertItem(insertSlot, extracted, false);
            }
            int extractCount = startCount - extracted.getCount();
            source.extractItem(extractSlot, extractCount, false);
            toMove -= extractCount;
        }
    }

    private boolean isSlotFiltered(IItemHandler inventory, int slot) {
        if(inventory instanceof ItemStackHandlerFiltered)
            return ((ItemStackHandlerFiltered) inventory).hasFilter(slot);
        else
            return false;
    }

    /*private boolean isFiltered(ItemStack stack) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if(handler.matchesFilter(stack, handler.getFilter(slot)))
                return true;
        }
        return false;
    }*/

    @Override
    public void pullFiltered(EnumFacing facing) {
        IItemHandler itemHandler = getInventory(cart.getTile(), facing);
        if (itemHandler != null) {
            moveItems(itemHandler, handler, 16, true);
        }
    }

    @Override
    public void pullUnfiltered(EnumFacing facing) {
        IItemHandler itemHandler = getInventory(cart.getTile(), facing);
        if (itemHandler != null) {
            moveItems(itemHandler, handler, 16, false);
        }
    }

    @Override
    public void pushFiltered(EnumFacing facing) {
        IItemHandler itemHandler = getInventory(cart.getTile(), facing);
        if (itemHandler != null) {
            moveItems(handler, itemHandler, 16, true);
        }
    }

    @Override
    public void pushUnfiltered(EnumFacing facing) {
        IItemHandler itemHandler = getInventory(cart.getTile(), facing);
        if (itemHandler != null) {
            moveItems(handler, itemHandler, 16, false);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) handler;
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
        nbt.setTag("items", handler.serializeNBT());
        return nbt;
    }

    @Override
    public CartContent readFromNBT(@Nonnull NBTTagCompound nbt) {
        handler.deserializeNBT(nbt);
        return this;
    }
}
