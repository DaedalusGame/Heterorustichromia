package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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
import rustichromia.tile.TileEntityBasicMachine;
import rustichromia.tile.TileEntityHopperWood;
import rustichromia.tile.TileEntityMechTorch;

import javax.annotation.Nullable;

public class BlockHopperWood extends Block {
    public static final PropertyDirection facing = PropertyDirection.create("facing", facing -> facing != null && facing != EnumFacing.UP);
    public static final PropertyBool straight = PropertyBool.create("straight");

    public BlockHopperWood(Material materialIn) {
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
        return new BlockStateContainer(this,facing,straight);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        side = side.getOpposite();
        if(side == EnumFacing.UP)
            side = EnumFacing.DOWN;
        return getDefaultState().withProperty(facing,side).withProperty(straight,true);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(facing,EnumFacing.getFront(meta >> 1)).withProperty(straight,(meta & 1) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(facing).getIndex() << 1) | (state.getValue(straight) ? 1 : 0);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntityHopperWood)
            return ((TileEntityHopperWood) tile).activate(world,pos,state,player,hand,facing,hitX,hitY,hitZ);
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntityHopperWood)
            ((TileEntityHopperWood) tile).breakBlock(world,pos,state,null);
    }


    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityHopperWood();
    }
}
