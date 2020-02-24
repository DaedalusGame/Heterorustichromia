package rustichromia.block;

import mysticalmechanics.tileentity.TileEntityGearbox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustichromia.tile.TileEntityRatiobox;
import rustichromia.util.Misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {

        List<AxisAlignedBB> checkBoxes = new ArrayList<>();
        checkBoxes.add(new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75));

        if (worldIn.getTileEntity(pos) instanceof TileEntityRatiobox) {
            TileEntityRatiobox ratiobox = ((TileEntityRatiobox) worldIn.getTileEntity(pos));

            if (ratiobox.hasAxle(EnumFacing.UP))
                checkBoxes.add(new AxisAlignedBB(0.375, 0.625, 0.375, 0.625, 1.0, 0.625));
            if (ratiobox.hasAxle(EnumFacing.DOWN))
                checkBoxes.add(new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 0.375, 0.625));
            if (ratiobox.hasAxle(EnumFacing.NORTH))
                checkBoxes.add(new AxisAlignedBB(0.375, 0.375, 0.0, 0.625, 0.625, 0.375));
            if (ratiobox.hasAxle(EnumFacing.SOUTH))
                checkBoxes.add(new AxisAlignedBB(0.375, 0.375, 0.625, 0.625, 0.625, 1.0));
            if (ratiobox.hasAxle(EnumFacing.WEST))
                checkBoxes.add(new AxisAlignedBB(0.0, 0.375, 0.375, 0.375, 0.625, 0.625));
            if (ratiobox.hasAxle(EnumFacing.EAST))
                checkBoxes.add(new AxisAlignedBB(0.625, 0.375, 0.375, 1.0, 0.625, 0.625));
        }

        for (AxisAlignedBB aabb :  checkBoxes) {
            addCollisionBoxToList(pos,entityBox,collidingBoxes,aabb);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        double x1 = 0.25;
        double y1 = 0.25;
        double z1 = 0.25;
        double x2 = 0.75;
        double y2 = 0.75;
        double z2 = 0.75;

        if (source.getTileEntity(pos) instanceof TileEntityRatiobox) {
            TileEntityRatiobox ratiobox = ((TileEntityRatiobox) source.getTileEntity(pos));
            if (ratiobox.hasAxle(EnumFacing.UP)) {
                y2 = 1;
            }
            if (ratiobox.hasAxle(EnumFacing.DOWN)) {
                y1 = 0;
            }
            if (ratiobox.hasAxle(EnumFacing.NORTH)) {
                z1 = 0;
            }
            if (ratiobox.hasAxle(EnumFacing.SOUTH)) {
                z2 = 1;
            }
            if (ratiobox.hasAxle(EnumFacing.WEST)) {
                x1 = 0;
            }
            if (ratiobox.hasAxle(EnumFacing.EAST)) {
                x2 = 1;
            }
        }

        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        List<AxisAlignedBB> subBoxes = new ArrayList<>();

        subBoxes.add(new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75));

        if (world.getTileEntity(pos) instanceof TileEntityRatiobox) {
            TileEntityRatiobox ratiobox = ((TileEntityRatiobox) world.getTileEntity(pos));

            if (ratiobox.hasAxle(EnumFacing.UP))
                subBoxes.add(new AxisAlignedBB(0.375, 0.625, 0.375, 0.625, 1.0, 0.625));
            if (ratiobox.hasAxle(EnumFacing.DOWN))
                subBoxes.add(new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 0.375, 0.625));
            if (ratiobox.hasAxle(EnumFacing.NORTH))
                subBoxes.add(new AxisAlignedBB(0.375, 0.375, 0.0, 0.625, 0.625, 0.375));
            if (ratiobox.hasAxle(EnumFacing.SOUTH))
                subBoxes.add(new AxisAlignedBB(0.375, 0.375, 0.625, 0.625, 0.625, 1.0));
            if (ratiobox.hasAxle(EnumFacing.WEST))
                subBoxes.add(new AxisAlignedBB(0.0, 0.375, 0.375, 0.375, 0.625, 0.625));
            if (ratiobox.hasAxle(EnumFacing.EAST))
                subBoxes.add(new AxisAlignedBB(0.625, 0.375, 0.375, 1.0, 0.625, 0.625));
        }

        return Misc.raytraceMultiAABB(subBoxes, pos, start, end);
    }
}
