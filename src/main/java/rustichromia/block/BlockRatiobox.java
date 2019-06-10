package rustichromia.block;

import mysticalmechanics.tileentity.TileEntityGearbox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityRatiobox;

import javax.annotation.Nullable;

public class BlockRatiobox extends Block {
    public static final PropertyDirection input = PropertyDirection.create("input");

    public BlockRatiobox(Material materialIn) {
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
        return new BlockStateContainer(this,input);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if(placer != null && !placer.isSneaking())
            side = side.getOpposite();
        return getDefaultState().withProperty(input,side);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(input,EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(input).getIndex();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRatiobox();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        TileEntityRatiobox tile = (TileEntityRatiobox)world.getTileEntity(pos);
        tile.updateNeighbors();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityRatiobox tile = (TileEntityRatiobox)world.getTileEntity(pos);
        return tile.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityRatiobox tile = (TileEntityRatiobox)world.getTileEntity(pos);
        tile.breakBlock(world,pos,state,player);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing side) {
        TileEntityGearbox tile = (TileEntityGearbox)world.getTileEntity(pos);
        tile.rotateTile(world, pos, side);
        return true;
    }
}
