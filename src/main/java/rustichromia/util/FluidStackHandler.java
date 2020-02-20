package rustichromia.util;

import mezz.jei.plugins.vanilla.ingredients.FluidStackHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class FluidStackHandler implements IFluidHandler, INBTSerializable<NBTTagCompound> {
    public class Slot implements IFluidTankProperties {
        FluidStack contents;
        int index;

        public Slot(int index) {
            this.index = index;
        }

        @Nullable
        @Override
        public FluidStack getContents() {
            return contents;
        }

        @Override
        public int getCapacity() {
            return FluidStackHandler.this.getCapacity(index);
        }

        @Override
        public boolean canFill() {
            return FluidStackHandler.this.canFill(index);
        }

        @Override
        public boolean canDrain() {
            return FluidStackHandler.this.canDrain(index);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return canFill() && FluidStackHandler.this.canFillFluidType(index,fluidStack);
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return canDrain() && FluidStackHandler.this.canDrainFluidType(index,fluidStack);
        }

        public int fill(FluidStack resource, boolean doFill) {
            if (resource == null || !canFillFluidType(resource) || (contents != null && !contents.isFluidEqual(resource))) {
                return 0;
            }

            int amount = contents == null ? 0 : contents.amount;
            int toInsert = Math.min(resource.amount, getCapacity() - amount);
            if (doFill) {
                if(contents == null)
                    contents = resource.copy();
                contents.amount = amount + toInsert;
                onContentsChanged(index);
            }
            return toInsert;
        }

        @Nullable
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (resource == null || !canDrainFluidType(resource) || (contents != null && !contents.isFluidEqual(resource))) {
                return null;
            }
            return drain(resource.amount, doDrain);
        }

        @Nullable
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if(contents == null || !canDrain())
                return null;
            FluidStack copy = contents.copy();
            copy.amount = Math.min(maxDrain, contents.amount);
            if (doDrain) {
                contents.amount -= copy.amount;
                if(contents.amount <= 0)
                    contents = null;
                onContentsChanged(index);
            }
            return copy;
        }

        public void writeToNBT(NBTTagCompound nbt) {
            if (contents != null)
                nbt.setTag("slot"+index, contents.writeToNBT(new NBTTagCompound()));
            else
                nbt.setString("slot"+index, "empty");
        }

        public void readFromNBT(NBTTagCompound nbt) {
            if(nbt.hasKey("slot"+index)){
                contents = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("slot"+index));
            }
        }
    }

    protected List<Slot> slots = new ArrayList<>();

    public FluidStackHandler(int slots) {
        for(int i = 0; i < slots; i++)
            this.slots.add(new Slot(i));
    }

    protected void onContentsChanged(int slot) {
        //NOOP
    }

    protected abstract int getCapacity(int index);

    protected abstract boolean canFill(int index);

    protected abstract boolean canDrain(int index);

    protected boolean canFillFluidType(int index, FluidStack resource) {
        return canFill(index);
    }

    protected boolean canDrainFluidType(int index, FluidStack resource) {
        return canDrain(index);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return slots.toArray(new IFluidTankProperties[slots.size()]);
    }

    public boolean contains(FluidStack stack) {
        for (Slot slot : slots) {
            if(slot.contents != null && slot.contents.isFluidEqual(stack))
                return true;
        }
        return false;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        boolean hasFluidStored = contains(resource);
        for (Slot slot : slots) {
            if (!hasFluidStored || (slot.contents != null && slot.contents.isFluidEqual(resource))) {
                int filled = slot.fill(resource, !doFill);
                if (filled > 0)
                    return filled;
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        for (Slot slot : slots) {
            FluidStack extracted = slot.drain(resource, !doDrain);
            if (extracted != null)
                return extracted;
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        for (Slot slot : slots) {
            FluidStack extracted = slot.drain(maxDrain, !doDrain);
            if (extracted != null)
                return extracted;
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Slot slot : slots)
            slot.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for (Slot slot : slots)
            slot.readFromNBT(nbt);
    }
}
