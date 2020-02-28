package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityRatiobox;
import rustichromia.tile.TileEntityWindVane;

import javax.annotation.Nullable;

public class BlockWindVane extends Block {
    public BlockWindVane(Material materialIn) {
        super(materialIn);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.5 - 0.25, 0.0, 0.5 - 0.25, 0.5 + 0.25, 1.0, 0.5 + 0.25);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.5 - 0.0625, 0.0, 0.5 - 0.0625, 0.5 + 0.0625, 1.0, 0.5 + 0.0625);
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing side) {
        TileEntityWindVane tile = (TileEntityWindVane)world.getTileEntity(pos);
        tile.rotateTile(world, pos, side);
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityWindVane();
    }
}
