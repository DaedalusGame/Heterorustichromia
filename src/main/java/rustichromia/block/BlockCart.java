package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityCart;

import javax.annotation.Nullable;

public class BlockCart extends Block {
    static final ThreadLocal<Boolean> moving = ThreadLocal.withInitial(() -> false);

    public BlockCart(Material materialIn) {
        super(materialIn);
    }

    public static void setMoving(boolean on) {
        moving.set(on);
    }

    public static boolean isMoving() {
        return moving.get();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCart();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!isMoving()) {
            TileEntityCart cart = (TileEntityCart) worldIn.getTileEntity(pos);
            if (cart != null) {
                cart.breakBlock(worldIn, pos, state, null);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
