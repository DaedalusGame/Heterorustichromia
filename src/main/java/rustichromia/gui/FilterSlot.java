package rustichromia.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import rustichromia.util.ItemStackHandlerFiltered;

import javax.annotation.Nonnull;

public class FilterSlot extends Slot {
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final ItemStackHandlerFiltered itemHandler;
    private final int index;

    public FilterSlot(ItemStackHandlerFiltered itemHandler, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        if (stack.isEmpty() || !itemHandler.isItemValid(index, stack))
            return false;

        IItemHandler handler = itemHandler;
        ItemStack remainder = handler.insertItem(index, stack, true);
        return remainder.getCount() < stack.getCount();
    }

    @Override
    @Nonnull
    public ItemStack getStack()
    {
        return itemHandler.getStackInSlot(index);
    }

    @Override
    public void putStack(@Nonnull ItemStack stack)
    {
        itemHandler.setFilter(index, stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_)
    {

    }

    @Override
    public int getSlotStackLimit()
    {
        return this.itemHandler.getSlotLimit(this.index);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack)
    {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);

        IItemHandler handler = itemHandler;
        ItemStack currentStack = handler.getStackInSlot(index);

            ItemStack remainder = handler.insertItem(index, maxAdd, true);

            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;

    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount)
    {
        return ((IItemHandler) itemHandler).extractItem(index, amount, false);
    }

    @Override
    public boolean isSameInventory(Slot other)
    {
        return false;
    }
}
