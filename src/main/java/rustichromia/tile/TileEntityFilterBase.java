package rustichromia.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.cart.CartData;
import rustichromia.util.ItemStackHandlerFiltered;

public abstract class TileEntityFilterBase extends TileEntity {
    public abstract boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ);

    public abstract void setFilter(CartData cart);
}
