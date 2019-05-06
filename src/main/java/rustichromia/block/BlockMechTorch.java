package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityMechTorch;
import rustichromia.tile.TileEntityWindmill;

import javax.annotation.Nullable;

public class BlockMechTorch extends Block {
    public static final PropertyDirection facing = PropertyDirection.create("facing");
    public static final PropertyBool on = PropertyBool.create("on");

    public BlockMechTorch(Material blockMaterialIn) {
        super(blockMaterialIn);
        setDefaultState(getDefaultState().withProperty(on,false));
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
        return new BlockStateContainer(this,facing,on);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(facing,side);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(facing,EnumFacing.getFront(meta >> 1)).withProperty(on,(meta & 1) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(facing).getIndex() << 1) | (state.getValue(on) ? 1 : 0);
    }

    /*@Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        TileEntityMechTorch p = (TileEntityMechTorch)world.getTileEntity(pos);
        p.updateNeighbors();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile != null && tile instanceof TileEntityMechTorch) {
            ((TileEntityMechTorch)tile).updateNeighbors();
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityWindmill p = (TileEntityMechTorch)world.getTileEntity(pos);
        p.breakBlock(world,pos,state,player);
    }*/

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMechTorch();
    }
}
