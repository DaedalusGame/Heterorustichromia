package rustichromia.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import rustichromia.tile.TileEntityFilterItem;

public class ContainerFilterItem extends Container {
    TileEntityFilterItem filter;

    public ContainerFilterItem(EntityPlayer player, TileEntityFilterItem filter) {
        this.filter = filter;

        int index = 0;
        int slotCount = filter.getInventory().getSlots();
        int rows = getRows(slotCount);
        int columns = slotCount / rows;
        for(int y = 0; y < rows; y++) {
            int ycolumns;
            if(y < rows-1)
                ycolumns = columns;
            else
                ycolumns = slotCount % columns;
            /*for(int x = 0; x < COLUMNS; x++) {
                this.addSlotToContainer(new FilterSlot(filter.getInventory(),index, 44 + x * 18, 8 + y * 18));
                index++;
            }*/
        }

        bindPlayerInventory(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null));
    }

    private int getRows(int slots) {
        return slots / 9;
    }

    protected void bindPlayerInventory(IItemHandler inventoryPlayer) {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                addSlotToContainer(new SlotItemHandler(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int i = 0; i < 9; i++)
        {
            addSlotToContainer(new SlotItemHandler(inventoryPlayer, i, 8 + i * 18, 84+58));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
