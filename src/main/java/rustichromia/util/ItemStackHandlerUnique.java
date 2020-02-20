package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ItemStackHandlerUnique implements IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
    public ItemStackHandler internal;

    public ItemStackHandlerUnique(ItemStackHandler internal) {
        this.internal = internal;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        internal.setStackInSlot(slot,stack);
    }

    @Override
    public int getSlots() {
        return internal.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return internal.getStackInSlot(slot);
    }

    private boolean hasItemStored(ItemStack stack) {
        for(int i = 0; i < getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);
            if(!slotStack.isEmpty() && ItemHandlerHelper.canItemStacksStack(slotStack, stack))
                return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        ItemStack slotStack = getStackInSlot(slot);
        if((!slotStack.isEmpty() && ItemHandlerHelper.canItemStacksStack(slotStack, stack)) || !hasItemStored(stack))
            return internal.insertItem(slot,stack,simulate);
        else
            return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return internal.extractItem(slot,amount,simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return internal.getSlotLimit(slot);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return internal.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        internal.deserializeNBT(nbt);
    }
}
