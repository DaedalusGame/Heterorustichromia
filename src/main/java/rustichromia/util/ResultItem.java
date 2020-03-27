package rustichromia.util;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.Rustichromia;

public class ResultItem extends Result {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "item");

    static {
        register(RESOURCE_LOCATION, ResultItem::new);
    }

    ItemStack stack;

    public ResultItem() {
        this(ItemStack.EMPTY);
    }

    public ResultItem(ItemStack stack) {
        super(RESOURCE_LOCATION);
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public int getItemCount() {
        return stack.getCount();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public Result transform() {
        return new ResultItem(stack.copy());
    }

    @Override
    public void drop(World world, BlockPos pos) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
    }

    @Override
    public void output(ItemBuffer buffer) {
        stack = buffer.ejectItem(stack);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        stack.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        stack = new ItemStack(compound);
    }

    @Override
    public ItemStack getJEIStack() {
        return stack;
    }
}
