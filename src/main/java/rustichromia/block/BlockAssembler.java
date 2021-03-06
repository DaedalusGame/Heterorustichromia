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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.Rustichromia;
import rustichromia.gui.GuiHandler;
import rustichromia.tile.TileEntityAssembler;
import rustichromia.tile.TileEntityBasicMachine;

import javax.annotation.Nullable;

public abstract class BlockAssembler extends Block {
    public static final PropertyDirection facing = PropertyDirection.create("facing",(facing) -> facing.getAxis().isHorizontal());
    protected int slots;

    public BlockAssembler(Material materialIn, int slots) {
        super(materialIn);
        this.slots = slots;
    }

    public abstract ItemStack getDisplayGear();

    public abstract int getTier();

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
        EnumFacing facing = placer != null ? placer.getHorizontalFacing() : side;
        if(facing.getAxis().isVertical())
            facing = EnumFacing.NORTH;
        return getDefaultState().withProperty(BlockAssembler.facing, facing.getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(facing,EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(facing).getHorizontalIndex();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntityBasicMachine)
            return ((TileEntityBasicMachine) tile).activate(world,pos,state,player,hand,facing,hitX,hitY,hitZ);
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileEntityBasicMachine)
            ((TileEntityBasicMachine) tile).breakBlock(world,pos,state,null);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityAssembler(slots);
    }
}
