package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ItemStackHandlerFiltered implements IItemHandler, INBTSerializable<NBTTagCompound> {
    public ItemStackHandler internal;
    public HashMap<Integer, ItemStack> filters = new HashMap<>();

    public ItemStackHandlerFiltered(int slots) {
        this.internal = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                ItemStackHandlerFiltered.this.onContentsChanged();
            }
        };
    }

    public boolean hasFilter(int slot) {
        return !getFilter(slot).isEmpty();
    }

    public ItemStack getFilter(int slot) {
        ItemStack filter = filters.get(slot);
        if(filter != null)
            return filter;
        else
            return ItemStack.EMPTY;
    }

    public void setFilter(int slot, ItemStack filter) {
        if(filter.isEmpty())
            filters.remove(slot);
        else
            filters.put(slot,filter);
    }

    public void copyFilters(ItemStackHandlerFiltered other) {
        filters.clear();
        for (Map.Entry<Integer, ItemStack> entry : other.filters.entrySet()) {
            filters.put(entry.getKey(),entry.getValue());
        }
    }

    public boolean matchesFilter(ItemStack stack, ItemStack filter) {
        if(filter.isEmpty())
            return true;
        return ItemHandlerHelper.canItemStacksStack(stack, filter);
    }

    @Override
    public int getSlots() {
        return internal.getSlots();
    }

    public void onContentsChanged() {
        //NOOP
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return internal.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(matchesFilter(stack, getFilter(slot)))
            return internal.insertItem(slot,stack,simulate);
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return internal.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return internal.getSlotLimit(slot);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = internal.serializeNBT();
        NBTTagList filterList = new NBTTagList();
        for (int slot = 0; slot < filters.size(); slot++) {
            if (hasFilter(slot)) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", slot);
                getFilter(slot).writeToNBT(itemTag);
                filterList.appendTag(itemTag);
            }
        }
        nbt.setTag("Filters", filterList);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        internal.deserializeNBT(nbt);
        NBTTagList tagList = nbt.getTagList("Filters", Constants.NBT.TAG_COMPOUND);
        filters.clear();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < filters.size()) {
                setFilter(slot, new ItemStack(itemTags));
            }
        }
    }
}
