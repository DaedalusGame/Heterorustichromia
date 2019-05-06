package rustichromia.api;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IExtruder {
    boolean canFlowInto(World world, BlockPos from, BlockPos to, EnumFacing facing, Block fluid);

    boolean canPushInto(World world, BlockPos from, BlockPos to, EnumFacing facing, Block fluid);

    boolean flow(World world, BlockPos from, BlockPos to, EnumFacing facing, Block fluid);
}
