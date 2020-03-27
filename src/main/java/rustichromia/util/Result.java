package rustichromia.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.Rustichromia;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public abstract class Result {
    private static final HashMap<ResourceLocation, Supplier<Result>> deserializers = new HashMap<>();

    public static void register(ResourceLocation resourceLocation, Supplier<Result> supplier) {
        deserializers.put(resourceLocation, supplier);
    }

    public static Result deserialize(NBTTagCompound compound) {
        ResourceLocation resLoc = new ResourceLocation(compound.getString("type"));
        try {
            Supplier<Result> supplier = deserializers.get(resLoc);
            Result result = supplier.get();
            result.readFromNBT(compound);
            return result;
        } catch (Exception e) {
            System.out.println("Failed to deserialize result '"+resLoc+"'");
        }
        return Result.EMPTY;
    }

    private ResourceLocation resourceLocation;

    public Result(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public static final Result EMPTY = new Result(new ResourceLocation(Rustichromia.MODID, "empty")) {
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void drop(World world, BlockPos pos) {
            //NOOP
        }

        @Override
        public void output(ItemBuffer buffer) {
            //NOOP
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            //NOOP
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            //NOOP
        }
    };

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public abstract boolean isEmpty();

    /**
     * Called before adding this result to the result buffer
     * @return The result itself, or one derived from it.
     */
    public Result transform() {
        return this;
    }

    public abstract void drop(World world, BlockPos pos);

    public abstract void output(ItemBuffer buffer);

    public int getItemCount() {
        return 0;
    }

    public abstract void writeToNBT(NBTTagCompound compound);

    public abstract void readFromNBT(NBTTagCompound compound);

    public ItemStack getJEIStack() {
        return ItemStack.EMPTY;
    }

    public void getJEITooltip(List<String> tooltip) {
        //NOOP
    }
}
