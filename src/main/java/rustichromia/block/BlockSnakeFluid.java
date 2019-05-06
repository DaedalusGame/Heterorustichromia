package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rustichromia.api.IExtruder;
import rustichromia.handler.PistonHandler;
import rustichromia.util.DimensionPos;
import rustichromia.util.SnakeFluidHelper;

import java.util.*;

public class BlockSnakeFluid extends Block {
    private static final int MOVE = 1;
    private static final int DELETED = 2;

    private static Map<DimensionPos,Integer> checkSet = new HashMap<>();
    private static List<SnakeFluidHelper> runningHelpers = new ArrayList<>();
    private static Material material = new Material(MapColor.WHITE_STAINED_HARDENED_CLAY) {
        @Override
        public EnumPushReaction getMobilityFlag() {
            return EnumPushReaction.DESTROY;
        }

        @Override
        public boolean isLiquid() {
            return true;
        }
    };

    public static final PropertyBool isTrail = PropertyBool.create("trail");

    public boolean isHead(IBlockState state) {
        return !state.getValue(isTrail);
    }

    public boolean isTail(IBlockState state) {
        return state.getValue(isTrail);
    }

    public BlockSnakeFluid(MapColor blockMapColorIn) {
        super(material, blockMapColorIn);
        setDefaultState(getDefaultState().withProperty(isTrail,false));
        setTickRandomly(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0,0,0,0,0,0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,isTrail);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(isTrail) ? 1 : 0;
    }

    @Override
    public int tickRate(World worldIn) {
        return 10;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(isTrail,meta > 0);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return isTail(worldIn.getBlockState(pos));
    }

    public boolean canFlowInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        if(state.getBlock() instanceof IExtruder)
            return ((IExtruder) state.getBlock()).canFlowInto(world,pos.offset(direction.getOpposite()),pos,direction,this);
        else if(state.getBlock().isReplaceable(world,pos) || world.getBlockState(pos).getBlock().isFlammable(world, pos, direction.getOpposite()))
            return true;
        else
            return false;
    }

    public boolean canPushInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        if(state.getBlock() instanceof IExtruder)
            return ((IExtruder) state.getBlock()).canPushInto(world,pos.offset(direction.getOpposite()),pos,direction,this);
        else if(canFlowInto(world,pos,state, direction))
            return true;
        else
            return false;
    }

    public boolean couldFlowInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        return canFlowInto(world,pos,state, direction) || state.getBlock() == this;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos,this, isHead(state) ? 1 : 10);
        //worldIn.updateBlockTick(pos,this, isHead(state) ? 1 : 10,0);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.scheduleUpdate(pos,this, isHead(state) ? 1 : 10);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(isHead(state))
            tryFlow(worldIn,pos);
        else
            worldIn.setBlockToAir(pos);
    }

    private void tryFlow(World world, BlockPos pos) {
        BlockPos downPos = pos.down();
        IBlockState downState = world.getBlockState(downPos);
        if(canFlowInto(world,downPos, downState, EnumFacing.DOWN)) {
            flow(world,pos,downPos,EnumFacing.DOWN);
        } else if(!world.isRemote) {
            checkSet.put(new DimensionPos(pos,world.provider.getDimension()),MOVE);
        }
    }

    public boolean flow(World world, BlockPos from, BlockPos to, EnumFacing direction) {
        IBlockState fromState = world.getBlockState(from);
        IBlockState toState = world.getBlockState(to);
        if(toState.getBlock() instanceof IExtruder) {
            boolean success = ((IExtruder) toState.getBlock()).flow(world, from, to, direction, this);
            if(!success)
                return false;
        } else
            world.setBlockState(to, fromState);
        world.setBlockState(from, fromState.withProperty(isTrail,true));
        world.scheduleUpdate(from,this,10);
        return true;
    }

    public boolean push(World world, BlockPos from, BlockPos to, EnumFacing direction) {
        IBlockState toState = world.getBlockState(to);
        if(toState.getBlock() instanceof IExtruder)
            return ((IExtruder) toState.getBlock()).flow(world, from, to, direction, this);
        else
            world.setBlockState(to, getDefaultState());
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if(isHead(state) && !worldIn.isRemote)
            checkSet.put(new DimensionPos(pos,worldIn.provider.getDimension()),DELETED);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        //if(isHead(state))
        //    checkSet.put(new DimensionPos(pos,worldIn.provider.getDimension()),DELETED);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            int cost = 0;
            for (Map.Entry<DimensionPos, Integer> entry : checkSet.entrySet()) {
                BlockPos pos = entry.getKey().getPos();
                int dimension = entry.getKey().getDimension();
                int type = entry.getValue();
                switch (type) {
                    case MOVE:
                        runningHelpers.add(new SnakeFluidHelper(entry.getKey(),this));
                        break;
                    case DELETED:
                        EnumFacing pushFacing = PistonHandler.getPushDirection(entry.getKey());
                        cost += tryPush(pos, dimension, pushFacing);
                        break;
                }
            }
            checkSet.clear();
            HashSet<DimensionPos> helperPositions = new HashSet<>();
            Iterator<SnakeFluidHelper> iterator = runningHelpers.iterator();
            while(iterator.hasNext() && cost < 10000) {
                SnakeFluidHelper helper = iterator.next();
                if(helperPositions.contains(helper)) {
                    iterator.remove();
                    break;
                }
                helperPositions.add(helper.getPos());
                cost += helper.calculate();
                if(helper.isDone()) {
                    helper.solve();
                    iterator.remove();
                }
            }
        }
    }

    private int tryPush(BlockPos pos, int dimension, EnumFacing pushFacing) {
        World world = DimensionManager.getWorld(dimension);
        if(pushFacing != null && canPushInto(world,pos.offset(pushFacing),world.getBlockState(pos.offset(pushFacing)), pushFacing)) {
            push(world,pos,pos.offset(pushFacing),pushFacing);
            //world.setBlockState(pos.offset(pushFacing),getDefaultState());
            return 1;
        } else {
            boolean done = false;
            if(canPushInto(world,pos.down(),world.getBlockState(pos.down()), EnumFacing.DOWN)) {
                push(world,pos,pos.down(),EnumFacing.DOWN);
                done = true;
            }
            if(!done)
            for(EnumFacing facing : EnumFacing.HORIZONTALS) {
                if(canPushInto(world,pos.offset(facing),world.getBlockState(pos.offset(facing)), facing)) {
                    push(world,pos,pos.offset(facing),facing);
                    done = true;
                    break;
                }
            }
            if (!done && canPushInto(world, pos.up(), world.getBlockState(pos.up()), EnumFacing.UP)) {
                push(world,pos,pos.up(),EnumFacing.UP);
                done = true;
            }
            if(!done) { //Explode

            }
            return 10;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
