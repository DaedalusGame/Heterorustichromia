package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityCartControl;
import rustichromia.util.CartUtil;

import javax.annotation.Nullable;

public class BlockCartControl extends BlockDirectional {
    private static final double thickness = 0.01;
    public static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0,1-thickness,0,1,1,1);
    public static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0,0,0,1,thickness,1);
    public static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0,0,0,thickness,1,1);
    public static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(1-thickness,0,0,1,1,1);
    public static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0,0,0,1,1,thickness);
    public static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0,0,1-thickness,1,1,1);

    public BlockCartControl(Material materialIn) {
        super(materialIn);
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, facing.getOpposite());
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        BlockPos mainPos = pos.offset(side.getOpposite());
        if(CartUtil.hasControl(worldIn,mainPos))
            return false;
        return super.canPlaceBlockOnSide(worldIn, pos, side);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if(!world.isRemote && !canBlockStay(world, pos, state)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos mainPos = pos.offset(facing);
        if(CartUtil.hasMultipleControls(world,mainPos))
            return false;
        return true;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch(state.getValue(FACING)){
            case DOWN:
                return AABB_DOWN;
            case UP:
                return AABB_UP;
            case NORTH:
                return AABB_NORTH;
            case SOUTH:
                return AABB_SOUTH;
            case WEST:
                return AABB_WEST;
            case EAST:
                return AABB_EAST;
        }
        return NULL_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCartControl();
    }
}
