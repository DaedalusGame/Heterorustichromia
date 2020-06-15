package rustichromia.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.Rustichromia;
import rustichromia.cart.CartContent;
import rustichromia.cart.CartData;
import rustichromia.cart.content.ContentItems;
import rustichromia.gui.GuiHandler;
import rustichromia.util.ItemStackHandlerFiltered;

public class TileEntityFilterItem extends TileEntityFilterBase {
    ItemStackHandlerFiltered inventory;

    public TileEntityFilterItem() {
        super();
        inventory = new ItemStackHandlerFiltered(8);
    }

    public ItemStackHandlerFiltered getInventory() {
        return inventory;
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        player.openGui(Rustichromia.MODID, GuiHandler.FILTER_ITEM, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void setFilter(CartData cart) {
        CartContent content = cart.getContent();
        if(content instanceof ContentItems) {
            ((ContentItems) content).getItemHandler().copyFilters(inventory);
        } else if(content.isEmpty()) {
            cart.setContent(new ContentItems(ContentItems.SLOTS));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        return compound;
    }
}
