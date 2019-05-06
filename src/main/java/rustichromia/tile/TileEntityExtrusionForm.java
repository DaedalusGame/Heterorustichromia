package rustichromia.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import rustichromia.block.BlockExtrusionForm;

public class TileEntityExtrusionForm extends TileEntity {
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getValue(BlockExtrusionForm.facing);
    }

    public TileEntity getAttached() {
        EnumFacing facing = getFacing();
        return world.getTileEntity(pos.offset(facing.getOpposite()));
    }

    public IItemHandler getAttachedInventory() {
        EnumFacing facing = getFacing();
        TileEntity tile = getAttached();
        if(tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing))
            return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing);
        else
            return null;
    }

    public boolean canProcess(int n) {
        IItemHandler inventory = getAttachedInventory();
        if(inventory == null)
            return false;
        ItemStack stack = new ItemStack(Items.IRON_INGOT,n);
        for(int i = 0; i < inventory.getSlots() && !stack.isEmpty(); i++) {
            stack = inventory.insertItem(i, stack,true);
        }
        return stack.isEmpty();
    }

    public void process(int n) {
        IItemHandler inventory = getAttachedInventory();
        ItemStack stack = new ItemStack(Items.IRON_INGOT,n);
        for(int i = 0; i < inventory.getSlots() && !stack.isEmpty(); i++) {
            stack = inventory.insertItem(i, stack,false);
        }
    }
}
