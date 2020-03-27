package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityWindmill;

import javax.annotation.Nullable;

public abstract class BlockWindmill extends Block {
    public static final PropertyDirection facing = PropertyDirection.create("facing");

    public BlockWindmill(Material blockMaterialIn) {
        super(blockMaterialIn);
    }

    public abstract double getScale(World world, BlockPos pos, IBlockState state);

    public abstract int getMaxBlades(World world, BlockPos pos, IBlockState state);

    @Deprecated
    public abstract double getBladeWeight(World world, BlockPos pos, IBlockState state);

    public abstract double getPowerModifier(World world, BlockPos pos, IBlockState state);

    public abstract int getMinHeight(World world, BlockPos pos, IBlockState state);

    public abstract double getBladePower(World world, BlockPos pos, IBlockState state);

    public abstract double getBladePowerPenalty(World world, BlockPos pos, IBlockState state);

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
        return getDefaultState().withProperty(facing,side.getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(facing,EnumFacing.getFront(meta));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityWindmill p = (TileEntityWindmill)world.getTileEntity(pos);
        return p.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(facing).getIndex();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        TileEntityWindmill p = (TileEntityWindmill)world.getTileEntity(pos);
        p.updateNeighbors();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile != null && tile instanceof TileEntityWindmill) {
            ((TileEntityWindmill)tile).updateNeighbors();
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityWindmill p = (TileEntityWindmill)world.getTileEntity(pos);
        p.breakBlock(world,pos,state,player);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityWindmill();
    }
}
