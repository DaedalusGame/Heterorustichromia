package rustichromia.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustichromia.Registry;
import rustichromia.api.IAnimalFeed;
import rustichromia.util.DimensionPos;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSnakeHay extends BlockSnakeFluid implements IAnimalFeed {
    public BlockSnakeHay() {
        super(MapColor.YELLOW);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 0;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if(harvesting.get())
            return;
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    @Override
    public boolean canFlowInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        if (state.getBlock() == Registry.FEEDER && direction == EnumFacing.DOWN)
            return true;
        else if (state.getBlock().isReplaceable(world, pos))
            return true;
        else
            return false;
    }

    @Override
    public boolean canPushInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        if (canFlowInto(world, pos, state, direction))
            return true;
        else
            return false;
    }

    @Override
    public boolean couldFlowInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        return canFlowInto(world, pos, state, direction);
    }

    @Override
    public boolean flow(World world, BlockPos from, BlockPos to, EnumFacing direction) {
        IBlockState toState = world.getBlockState(to);
        if(toState.getBlock() == Registry.FEEDER)
            return false;
        return super.flow(world, from, to, direction);
    }

    @Override
    public void settle(World world, BlockPos pos) {
        world.scheduleUpdate(pos, this, 1);
    }

    @Override
    public void explode(World world, BlockPos pos, EnumFacing pushFacing) {
        //NOOP
    }

    @Override
    public int getMaxDistance() {
        return 2;
    }

    @Override
    public int getTrailLength() {
        return 5;
    }

    private boolean canFeedEntity(Entity entity) {
        if(entity instanceof EntityZombieHorse || entity instanceof EntitySkeletonHorse)
            return false;
        if(entity instanceof EntityHorse)
            return true;
        if(entity instanceof EntitySheep)
            return true;
        if(entity instanceof EntityCow)
            return true;
        if(entity instanceof EntityLlama)
            return true;
        if(entity instanceof EntityAnimal)
            return ((EntityAnimal) entity).isBreedingItem(new ItemStack(Items.WHEAT));
        return false;
    }

    @Override
    public boolean canFeed(Entity entity) {
        if(!canFeedEntity(entity))
            return false;
        if(entity instanceof EntityAnimal)
        {
            EntityAnimal animal = (EntityAnimal) entity;
            return animal.getGrowingAge() == 0 && !animal.isInLove();
        }
        return false;
    }

    @Override
    public void feed(Entity entity) {
        if(entity instanceof EntityAnimal)
        {
            ((EntityAnimal) entity).setInLove(null);
        }
    }
}
