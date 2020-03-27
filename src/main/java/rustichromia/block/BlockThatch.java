package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustichromia.Registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockThatch extends BlockFluidBase {
    AxisAlignedBB boxes[] = new AxisAlignedBB[16];

    public BlockThatch(Material material) {
        super(Registry.FLUID_THATCH, material);

        setRenderLayer(BlockRenderLayer.CUTOUT);

        for (int i = 0; i < 16; i++) {
            boxes[i] = new AxisAlignedBB(0, 0, 0, 1, Math.max(14f / 16f - i / 8f, 0.01f), 1);
        }
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 200;
    }

    @Nonnull
    @Override
    public IBlockState getExtendedState(@Nonnull IBlockState oldState, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        IExtendedBlockState state = (IExtendedBlockState)super.getExtendedState(oldState, world, pos);
        state = state.withProperty(FLOW_DIRECTION, (float)getFlowDirection2(world, pos));
        return state;

    }

    public double getFlowDirection2(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != this)
        {
            return -1000.0;
        }
        Vec3d vec = ((BlockFluidBase)state.getBlock()).getFlowVector(world, pos);
        return vec.x == 0.0D && vec.z == 0.0D ? -1000.0D : MathHelper.atan2(vec.z, vec.x) - Math.PI / 2D;
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
    public int getLightOpacity(IBlockState state) {
        return 0;
    }

    @Override
    public boolean isPassable(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return boxes[state.getValue(LEVEL)];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBox(@Nonnull IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        return getBoundingBox(blockState, worldIn, pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return getBoundingBox(blockState, worldIn, pos);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        if(getConnectivity(worldIn,pos) >= 6)
            return false;
        return super.canPlaceBlockAt(worldIn, pos);
    }

    @Override
    public void neighborChanged(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighbourPos) {
        checkConnectivity(state, world, pos);
    }

    private void checkConnectivity(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        IBlockState ground = world.getBlockState(pos.down());
        //if(ground.getBlockFaceShape(world,pos.down(),EnumFacing.UP) != BlockFaceShape.SOLID)
        //    world.setBlockToAir(pos);
        int level = getConnectivity(world, pos);
        if(level >= 6) {
            unravel(world,pos);
            world.setBlockToAir(pos);
        }
        else if(level != state.getValue(LEVEL))
            world.setBlockState(pos, state.withProperty(LEVEL,level));
    }

    private void unravel(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote && !world.restoringBlockSnapshots) {
            for(int i = 0; i < 3; i++) {
                spawnAsEntity(world, pos, new ItemStack(Registry.WHEAT_CHAFF));
            }
        }
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 1;
    }

    @Nonnull
    @Override
    public Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    private int getConnectivity(@Nonnull World world, @Nonnull BlockPos pos) {
        int level = 15;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos checkPos = pos.offset(facing);
            IBlockState checkState = world.getBlockState(checkPos);
            if(checkState.getBlock() == this)
                level = Math.min(level, checkState.getValue(LEVEL) + 1);
            else if(checkState.getBlock() == Registry.THATCH_BLOCK)
                level = Math.min(level, 1);
            //else if(checkState.getBlockFaceShape(world, checkPos, facing.getOpposite()) == BlockFaceShape.SOLID)
            //    level = 0;
        }
        IBlockState topState = world.getBlockState(pos.up());
        if(topState.getBlock() == this)
            level = Math.min(level, topState.getValue(LEVEL) - 1);
        else if(topState.getBlock() == Registry.THATCH_BLOCK)
            level = Math.min(level, 0);

        /*IBlockState bottomState = world.getBlockState(pos.down());
        if(bottomState.getBlock() == this)
            level = Math.min(level, bottomState.getValue(LEVEL) + 1);
        else if(bottomState.getBlock() == Registry.THATCH_BLOCK)
            level = Math.min(level, 1);*/
        return Math.max(level,0);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        checkConnectivity(state, worldIn, pos);
    }

    @Override
    public int getQuantaValue(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isAir(state, world, pos))
        {
            return 0;
        }

        if (state.getBlock() == Registry.THATCH_BLOCK)
        {
            return quantaPerBlock;
        }
        else if (state.getBlock() != this)
        {
            return -1;
        }

        return quantaPerBlock - state.getValue(LEVEL);
    }

    @Override
    public boolean canCollideCheck(@Nonnull IBlockState state, boolean fullHit) {
        return isCollidable();
    }

    @Override
    public int getMaxRenderHeightMeta() {
        return 0;
    }

    @Override
    public Fluid getFluid() {
        return null; //This is not the fluid you're looking for
    }

    @Override
    public int place(World world, BlockPos pos, @Nonnull FluidStack fluidStack, boolean doPlace) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(World world, BlockPos pos, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canDrain(World world, BlockPos pos) {
        return false;
    }

    @Override
    public float getFluidHeightAverage(float... flow) {
        int count = 0;
        for (float f : flow) {
            if(f >= 0)
                count++;
        }
        if(count <= 0)
            return 0;
        return super.getFluidHeightAverage(flow);
    }

    @Override
    public float getFluidHeightForRender(IBlockAccess world, BlockPos pos, @Nonnull IBlockState up) {
        IBlockState here = world.getBlockState(pos);
        if (here.getBlock() == this)
        {
            if (up.getBlock() == this || up.getBlock() == Registry.THATCH_BLOCK)
            {
                return 1;
            }

            if (getMetaFromState(here) == getMaxRenderHeightMeta())
            {
                return quantaFraction;
            }
        }
        /*if (here.getBlock() == this)
        {
            return Math.min(1 - BlockLiquid.getLiquidHeightPercent(here.getValue(BlockLiquid.LEVEL)), quantaFraction);
        }*/
        return !here.getMaterial().isSolid() && up.getBlock() == this ? 1 : Math.max(-1,this.getQuantaPercentage(world, pos) * quantaFraction);
    }

    @Override
    public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side)
    {
        IBlockState neighbor = world.getBlockState(pos.offset(side));
        if (neighbor.getBlock() == this || neighbor.getBlock() == Registry.THATCH_BLOCK)
        {
            return false;
        }
        return true;
    }
}
