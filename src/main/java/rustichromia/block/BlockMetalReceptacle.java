package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.api.IExtruder;

public class BlockMetalReceptacle extends Block implements IExtruder {
    public BlockMetalReceptacle(Material materialIn) {
        super(materialIn);
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
    public boolean canFlowInto(World world, BlockPos from, BlockPos to, EnumFacing facing, Block fluid) {
        BlockPos upPos = to.up();
        IBlockState upState = world.getBlockState(upPos);
        return facing == EnumFacing.DOWN;// && upState.getBlock().isReplaceable(world,upPos);
    }

    @Override
    public boolean canPushInto(World world, BlockPos from, BlockPos to, EnumFacing facing, Block fluid) {
        return false;
    }

    @Override
    public boolean flow(World world, BlockPos from, BlockPos to, EnumFacing facing, Block fluid) {
        return false;
    }
}
