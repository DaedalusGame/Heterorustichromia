package rustichromia.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class ItemBuffer implements INBTSerializable<NBTTagList>, Iterable<Result> {
    private static final int OVERFLOW_THRESHOLD = 100;

    TileEntity tile;
    LinkedList<Result> stacks = new LinkedList<>();

    public ItemBuffer(TileEntity tile) {
        this.tile = tile;
    }

    public void add(Result stack) {
        if(stack.isEmpty())
            return;
        stacks.add(stack);
        tile.markDirty();
        checkOverflow();
    }

    public void addAll(Collection<Result> stack) {
        stacks.addAll(stack);
        tile.markDirty();
        checkOverflow();
    }

    public void addFirst(Result stack) {
        if(stack.isEmpty())
            return;
        stacks.addFirst(stack);
    }

    public Result removeFirst() {
        if(stacks.isEmpty())
            return Result.EMPTY;
        tile.markDirty();
        return stacks.removeFirst();
    }

    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    public Result getTop() {
        if(stacks.isEmpty())
            return Result.EMPTY;
        return stacks.getLast();
    }

    public int totalItems() {
        return stacks.stream().mapToInt(Result::getItemCount).sum();
    }

    public int size() {
        return stacks.size();
    }

    public void dropAll(World world, BlockPos pos) {
        for (Result stack : stacks) {
            stack.drop(world, pos);
        }
    }

    public ItemStack ejectItem(ItemStack stack) {
        return stack;
    }

    public boolean ejectBlock(IBlockState state) {
        return false;
    }

    private void checkOverflow() {
        if(stacks.size() > OVERFLOW_THRESHOLD)
            System.out.println("["+tile.toString()+"] ItemBuffer is growing unchecked! Report this to the mod author!"); //TODO: replace with logger
    }

    @Override
    public NBTTagList serializeNBT() {
        NBTTagList tag = new NBTTagList();
        for (Result stack : stacks) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("type", stack.getResourceLocation().toString());
            stack.writeToNBT(compound);
            tag.appendTag(compound);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagList tag) {
        if(tag.getTagType() != 10)
            return;
        stacks.clear();
        for (NBTBase stack : tag) {
            NBTTagCompound compound = (NBTTagCompound) stack;
            Result result;
            if(compound.hasKey("type"))
                result = Result.deserialize(compound);
            else //Backwards compat
                result = new ResultItem(new ItemStack(compound));
            stacks.add(result);
        }
    }

    @Override
    public Iterator<Result> iterator() {
        return stacks.iterator();
    }
}
