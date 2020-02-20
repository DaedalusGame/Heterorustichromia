package rustichromia.block;

import mysticalmechanics.block.BlockGearbox;
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
import rustichromia.tile.TileEntityCrank;

import javax.annotation.Nullable;

public class BlockCrank extends Block {
    public static final PropertyDirection facing = PropertyDirection.create("facing");

    public BlockCrank(Material materialIn) {
        super(materialIn);
    }

    @Override
    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(facing).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState().withProperty(facing, EnumFacing.getFront(meta));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        //EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(pos, placer);
        EnumFacing facing = face;
        return getDefaultState().withProperty(BlockGearbox.facing, facing);
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCrank();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityCrank tile = (TileEntityCrank)world.getTileEntity(pos);
        return tile.activate(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntityCrank) {
            ((TileEntityCrank)tile).updateNeighbors();
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntityCrank p = (TileEntityCrank)world.getTileEntity(pos);
        p.breakBlock(world,pos,state,player);
    }
}
