package rustichromia.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.cart.CartData;
import rustichromia.tile.TileEntityCart;
import rustichromia.util.Misc;

public class ItemBlockCart extends ItemBlock {
    public ItemBlockCart(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

        if(success && !world.isRemote) {
            TileEntityCart tile = (TileEntityCart) world.getTileEntity(pos);
            if(tile != null) {
                EnumFacing controlFacing = Misc.getFaceOrientation(side, hitX, hitY, hitZ);
                tile.setData(CartData.create(tile.getPos(),Misc.getTrueFacing(controlFacing, side),side));
            }
        }
        return success;
    }
}
