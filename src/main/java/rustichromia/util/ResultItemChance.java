package rustichromia.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.Rustichromia;

import java.util.List;
import java.util.Random;

public class ResultItemChance extends Result {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "item_chance");
    static Random random = new Random();

    static {
        register(RESOURCE_LOCATION, ResultItem::new);
    }

    ItemStack stack;
    float chance;

    public ResultItemChance() {
        this(ItemStack.EMPTY,0);
    }

    public ResultItemChance(ItemStack stack, float chance) {
        super(RESOURCE_LOCATION);
        this.stack = stack;
        this.chance = chance;
    }

    public ItemStack getStack() {
        return stack;
    }

    public float getChance() {
        return chance;
    }

    @Override
    public ItemStack getJEIStack() {
        return stack;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty() || chance <= 0;
    }

    @Override
    public Result transform() {
        if(random.nextFloat() < chance)
            return new ResultItem(stack.copy());
        return Result.EMPTY;
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
        stack.writeToNBT(compound);
        compound.setFloat("chance", chance);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        stack = new ItemStack(compound);
        chance = compound.getFloat("chance");
    }

    @Override
    public void getJEITooltip(List<String> tooltip) {
        tooltip.add(1, I18n.format("rustichromia.tooltip.result.chance",chance*100));
    }
}
