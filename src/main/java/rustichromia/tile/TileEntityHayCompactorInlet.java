package rustichromia.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityHayCompactorInlet extends TileEntityMultiSlave {
    TileEntityHayCompactor master;
    IItemHandler inventoryProxy = new IItemHandler() {
        @Override
        public int getSlots() {
            TileEntityHayCompactor master = getMaster();
            if (master != null) {
                return master.inventory.getSlots();
            }
            return 0;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            TileEntityHayCompactor master = getMaster();
            if (master != null) {
                return master.inventory.getStackInSlot(slot);
            }
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            TileEntityHayCompactor master = getMaster();
            if (master != null) {
                return master.inventory.insertItem(slot,stack,simulate);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            TileEntityHayCompactor master = getMaster();
            if (master != null) {
                return master.inventory.getSlotLimit(slot);
            }
            return 0;
        }
    };

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventoryProxy;
        return super.getCapability(capability, facing);
    }

    @Nullable
    private TileEntityHayCompactor getMaster() {
        if(this.master == null || this.master.isInvalid()) {
            BlockPos masterPos = getPos().add(part.getMasterOffset());
            TileEntity master = getWorld().getTileEntity(masterPos);
            if(master instanceof TileEntityHayCompactor)
                this.master = (TileEntityHayCompactor) master;
        }
        return this.master;
    }
}
