package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.api.IExtruder;
import rustichromia.tile.TileEntityExtrusionForm;

import javax.annotation.Nullable;

public class BlockExtrusionForm extends Block implements IExtruder {
    public static final PropertyDirection facing = PropertyDirection.create("facing");

    public BlockExtrusionForm(Material materialIn) {
        super(materialIn);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,facing);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(facing,side);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(facing,EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(facing).getIndex();
    }

    @Override
    public boolean canFlowInto(World world, BlockPos from, BlockPos to, EnumFacing direction, Block fluid) {
        return false;
    }

    @Override
    public boolean canPushInto(World world, BlockPos from, BlockPos to, EnumFacing direction, Block fluid) {
        IBlockState state = world.getBlockState(to);
        TileEntityExtrusionForm tile = (TileEntityExtrusionForm) world.getTileEntity(to);
        return direction.getOpposite() == state.getValue(facing) && tile.canProcess(16);
    }

    @Override
    public boolean flow(World world, BlockPos from, BlockPos to, EnumFacing direction, Block fluid) {
        TileEntityExtrusionForm tile = (TileEntityExtrusionForm) world.getTileEntity(to);
        tile.process(16);
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityExtrusionForm();
    }
}
