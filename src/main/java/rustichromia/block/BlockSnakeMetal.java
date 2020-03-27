package rustichromia.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.Registry;
import rustichromia.api.IExtruder;

public class BlockSnakeMetal extends BlockSnakeFluid {
    public BlockSnakeMetal(MapColor blockMapColorIn) {
        super(blockMapColorIn);
    }

    @Override
    public boolean canFlowInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        if(state.getBlock() instanceof IExtruder)
            return ((IExtruder) state.getBlock()).canFlowInto(world,pos.offset(direction.getOpposite()),pos,direction,this);
        else if(state.getBlock().isReplaceable(world,pos) || world.getBlockState(pos).getBlock().isFlammable(world, pos, direction.getOpposite()))
            return true;
        else
            return false;
    }

    @Override
    public boolean canPushInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        if(state.getBlock() instanceof IExtruder)
            return ((IExtruder) state.getBlock()).canPushInto(world,pos.offset(direction.getOpposite()),pos,direction,this);
        else if(canFlowInto(world,pos,state, direction))
            return true;
        else
            return false;
    }

    @Override
    public boolean flow(World world, BlockPos from, BlockPos to, EnumFacing direction) {
        IBlockState toState = world.getBlockState(to);
        if(toState.getBlock() instanceof IExtruder) {
            boolean success = ((IExtruder) toState.getBlock()).flow(world, from, to, direction, this);
            if(!success)
                return false;
        }
        return super.flow(world, from, to, direction);
    }

    @Override
    public boolean push(World world, BlockPos from, BlockPos to, EnumFacing direction) {
        IBlockState toState = world.getBlockState(to);
        if(toState.getBlock() instanceof IExtruder)
            return ((IExtruder) toState.getBlock()).flow(world, from, to, direction, this);
        return super.push(world,from,to,direction);
    }

    @Override
    public void settle(World world, BlockPos pos) {
        world.setBlockState(pos, Registry.BLOCK_STEEL.getDefaultState());
    }

    @Override
    public void explode(World world, BlockPos pos, EnumFacing pushFacing) {

    }
}
