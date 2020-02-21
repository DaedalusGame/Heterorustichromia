package rustichromia.util;

import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.LinkedList;

public class ItemBuffer implements INBTSerializable<NBTTagList> {
    private static final int OVERFLOW_THRESHOLD = 100;

    TileEntity tile;
    LinkedList<ItemStack> stacks = new LinkedList<>();

    public ItemBuffer(TileEntity tile) {
        this.tile = tile;
    }

    public void add(ItemStack stack) {
        if(stack.isEmpty())
            return;
        stacks.add(stack);
        tile.markDirty();
        checkOverflow();
    }

    public void addAll(Collection<ItemStack> stack) {
        stacks.addAll(stack);
        tile.markDirty();
        checkOverflow();
    }

    public void addFirst(ItemStack stack) {
        if(stack.isEmpty())
            return;
        stacks.addFirst(stack);
    }

    public ItemStack removeFirst() {
        if(stacks.isEmpty())
            return ItemStack.EMPTY;
        tile.markDirty();
        return stacks.removeFirst();
    }

    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    public ItemStack getTop() {
        if(stacks.isEmpty())
            return ItemStack.EMPTY;
        return stacks.getLast();
    }

    public int totalItems() {
        return stacks.stream().mapToInt(ItemStack::getCount).sum();
    }

    public int size() {
        return stacks.size();
    }

    public void dropAll(World world, BlockPos pos) {
        for (ItemStack stack : stacks) {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }

    private void checkOverflow() {
        if(stacks.size() > OVERFLOW_THRESHOLD)
            System.out.println("["+tile.toString()+"] ItemBuffer is growing unchecked! Report this to the mod author!"); //TODO: replace with logger
    }

    @Override
    public NBTTagList serializeNBT() {
        NBTTagList tag = new NBTTagList();
        for (ItemStack stack : stacks) {
            tag.appendTag(stack.serializeNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagList tag) {
        if(tag.getTagType() != 10)
            return;
        stacks.clear();
        for (NBTBase stack : tag) {
            stacks.add(new ItemStack((NBTTagCompound) stack));
        }
    }
}
