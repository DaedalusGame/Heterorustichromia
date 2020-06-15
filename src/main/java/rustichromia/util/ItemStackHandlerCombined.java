package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public abstract class ItemStackHandlerCombined implements IItemHandler {

    public abstract IItemHandler getFirst();

    public abstract IItemHandler getSecond();

    private int getSlotsSafe(IItemHandler itemHandler) {
        if(itemHandler != null)
            return itemHandler.getSlots();
        return 0;
    }

    @Override
    public int getSlots() {
        return getSlotsSafe(getFirst()) + getSlotsSafe(getSecond());
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        IItemHandler first = getFirst();
        IItemHandler second = getSecond();
        int firstSlots = getSlotsSafe(first);
        if(first == null && second == null)
            return ItemStack.EMPTY;
        else if(first == null || slot >= firstSlots)
            return second.getStackInSlot(slot - firstSlots);
        else
            return first.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        IItemHandler first = getFirst();
        IItemHandler second = getSecond();
        int firstSlots = getSlotsSafe(first);
        if(first == null && second == null)
            return stack;
        else if(first == null || slot >= firstSlots)
            return second.insertItem(slot - firstSlots, stack, simulate);
        else
            return first.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        IItemHandler first = getFirst();
        IItemHandler second = getSecond();
        int firstSlots = getSlotsSafe(first);
        if(first == null && second == null)
            return ItemStack.EMPTY;
        else if(first == null || slot >= firstSlots)
            return second.extractItem(slot - firstSlots, amount, simulate);
        else
            return first.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        IItemHandler first = getFirst();
        IItemHandler second = getSecond();
        int firstSlots = getSlotsSafe(first);
        if(first == null && second == null)
            return 0;
        else if(first == null || slot >= firstSlots)
            return second.getSlotLimit(slot - firstSlots);
        else
            return first.getSlotLimit(slot);
    }
}
