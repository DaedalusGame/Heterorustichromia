package rustichromia.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import rustichromia.block.BlockSnakeFluid;

import java.util.*;

public class SnakeFluidHelper {
    private static Random random = new Random();
    private DimensionPos fluidPos;
    private Set<BlockPos> visited = new HashSet<>(); //blocks won't cross dimensions so this is fine
    private LinkedList<VisitPos> toVisit = new LinkedList<>();
    private List<EnumFacing> pits = new ArrayList<>();
    private BlockSnakeFluid block;
    boolean done = false;

    public DimensionPos getPos() {
        return fluidPos;
    }

    public SnakeFluidHelper(DimensionPos fluidPos, BlockSnakeFluid block) {
        this.fluidPos = fluidPos;
        this.block = block;
        BlockPos start = fluidPos.getPos();
        this.visited.add(start);
        for(EnumFacing facing : EnumFacing.HORIZONTALS)
            this.toVisit.add(new VisitPos(start.offset(facing),facing,1));
    }

    public int calculate() {
        World world = DimensionManager.getWorld(fluidPos.getDimension());
        if(!checkValid(world)) {
            done = true;
            return 1;
        }
        int cost = 0;
        while(!done && cost < 100) {
            VisitPos pos = toVisit.removeFirst();
            visit(world,pos.getPos(),pos.getFacing(),pos.getDist());
            cost++;
            if (toVisit.isEmpty() || (!pits.isEmpty() && toVisit.getFirst().getDist() > pos.getDist()))
                done = true;
        }

        return cost;
    }

    public void visit(World world, BlockPos pos, EnumFacing direction, int distance)
    {
        if(visited.contains(pos))
            return;
        visited.add(pos);
        IBlockState state = world.getBlockState(pos);
        if(block.couldFlowInto(world,pos,state, direction)) {
            BlockPos downPos = pos.down();
            IBlockState downState = world.getBlockState(downPos);
            if(block.couldFlowInto(world, downPos,downState, EnumFacing.DOWN)) {
                pits.add(direction);
            } else if(distance < block.getMaxDistance()) {
                for(EnumFacing facing : EnumFacing.HORIZONTALS)
                    this.toVisit.add(new VisitPos(pos.offset(facing),direction,distance+1));
            }
        }
    }

    public boolean isDone() {
        return done;
    }

    public void solve() {
        //if(pits.isEmpty())
        //    return;
        World world = DimensionManager.getWorld(fluidPos.getDimension());
        if(checkValid(world)) {
            BlockPos pos = fluidPos.getPos();
            if(pits.isEmpty()) {
                block.settle(world, pos);
            } else {
                int randIndex = random.nextInt(pits.size());
                for (int i = 0; i < pits.size(); i++) {
                    EnumFacing spreadDirection = pits.get((i + randIndex) % pits.size());
                    BlockPos spreadPos = pos.offset(spreadDirection);
                    if (block.canFlowInto(world, spreadPos, world.getBlockState(spreadPos), spreadDirection)) {
                        block.flow(world, pos, spreadPos, spreadDirection);
                        break;
                    } else {
                        world.scheduleUpdate(pos,block,1);
                    }
                }
            }
        }
    }

    private boolean checkValid(World world) {
        IBlockState state = world.getBlockState(fluidPos.getPos());
        return state.getBlock() == block && block.isHead(state);
    }

    private static class VisitPos {
        BlockPos pos;
        EnumFacing facing;
        int dist;

        public VisitPos(BlockPos pos, EnumFacing facing, int dist) {
            this.pos = pos;
            this.facing = facing;
            this.dist = dist;
        }

        public BlockPos getPos() {
            return pos;
        }

        public EnumFacing getFacing() {
            return facing;
        }

        public int getDist() {
            return dist;
        }
    }
}
