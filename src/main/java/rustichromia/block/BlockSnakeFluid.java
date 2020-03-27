package rustichromia.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rustichromia.handler.PistonHandler;
import rustichromia.util.DimensionPos;
import rustichromia.util.SnakeFluidHelper;

import javax.annotation.Nullable;
import java.util.*;

public abstract class BlockSnakeFluid extends Block {
    private static final int MOVE = 1;
    private static final int DELETED = 2;

    protected ThreadLocal<Boolean> harvesting = ThreadLocal.withInitial(() -> false);
    private Map<DimensionPos, Integer> checkSet = new HashMap<>();
    private List<SnakeFluidHelper> runningHelpers = new ArrayList<>();
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

    public static final PropertyInteger TRAIL = PropertyInteger.create("trail", 0, 15);

    public boolean isHead(IBlockState state) {
        return state.getValue(TRAIL) == 0;
    }

    public boolean isTail(IBlockState state) {
        return state.getValue(TRAIL) > 0;
    }

    public BlockSnakeFluid(MapColor blockMapColorIn) {
        super(material, blockMapColorIn);
        setDefaultState(getDefaultState().withProperty(TRAIL, 0));
        setTickRandomly(true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TRAIL);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TRAIL);
    }

    @Override
    public int tickRate(World worldIn) {
        return getDecayTime();
    }

    public int getDecayTime() {
        return 1;
    }

    public int getTrailLength() {
        return 15;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TRAIL, meta);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return isTail(worldIn.getBlockState(pos));
    }

    public abstract boolean canFlowInto(World world, BlockPos pos, IBlockState state, EnumFacing direction);

    public abstract boolean canPushInto(World world, BlockPos pos, IBlockState state, EnumFacing direction);

    public boolean couldFlowInto(World world, BlockPos pos, IBlockState state, EnumFacing direction) {
        return canFlowInto(world, pos, state, direction) || state.getBlock() == this;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote)
            worldIn.scheduleUpdate(pos, this, isHead(state) ? 1 : getDecayTime());
        //worldIn.updateBlockTick(pos,this, isHead(state) ? 1 : 10,0);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote)
            worldIn.scheduleUpdate(pos, this, isHead(state) ? 1 : getDecayTime());
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (isHead(state))
            tryFlow(worldIn, pos);
        else {
            int trailCount = state.getValue(TRAIL) + 1;
            if (trailCount > getTrailLength())
                worldIn.setBlockToAir(pos);
            else
                worldIn.setBlockState(pos, state.withProperty(TRAIL, trailCount));
        }
    }

    private void tryFlow(World world, BlockPos pos) {
        BlockPos downPos = pos.down();
        IBlockState downState = world.getBlockState(downPos);
        if (canFlowInto(world, downPos, downState, EnumFacing.DOWN)) {
            flow(world, pos, downPos, EnumFacing.DOWN);
        } else if (!world.isRemote) {
            checkSet.put(new DimensionPos(pos, world.provider.getDimension()), MOVE);
        }
    }

    public boolean flow(World world, BlockPos from, BlockPos to, EnumFacing direction) {
        IBlockState fromState = world.getBlockState(from);
        world.setBlockState(to, fromState);
        world.setBlockState(from, fromState.withProperty(TRAIL, 1));
        world.scheduleUpdate(from, this, getDecayTime());
        return true;
    }

    public boolean push(World world, BlockPos from, BlockPos to, EnumFacing direction) {
        world.setBlockState(to, getDefaultState());
        return true;
    }

    public abstract void settle(World world, BlockPos pos);

    public abstract void explode(World world, BlockPos pos, EnumFacing pushFacing);

    public int getMaxDistance() {
        return 128;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (isHead(state) && !worldIn.isRemote)
            checkSet.put(new DimensionPos(pos, worldIn.provider.getDimension()), DELETED);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        harvesting.set(true);
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        harvesting.set(false);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        //if(isHead(state))
        //    checkSet.put(new DimensionPos(pos,worldIn.provider.getDimension()),DELETED);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            int cost = 0;
            for (Map.Entry<DimensionPos, Integer> entry : checkSet.entrySet()) {
                BlockPos pos = entry.getKey().getPos();
                int dimension = entry.getKey().getDimension();
                int type = entry.getValue();
                switch (type) {
                    case MOVE:
                        runningHelpers.add(new SnakeFluidHelper(entry.getKey(), this));
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
            while (iterator.hasNext() && cost < 10000) {
                SnakeFluidHelper helper = iterator.next();
                if (helperPositions.contains(helper)) {
                    iterator.remove();
                    break;
                }
                helperPositions.add(helper.getPos());
                cost += helper.calculate();
                if (helper.isDone()) {
                    helper.solve();
                    iterator.remove();
                }
            }
        }
    }

    private int tryPush(BlockPos pos, int dimension, EnumFacing pushFacing) {
        World world = DimensionManager.getWorld(dimension);
        if (pushFacing != null && canPushInto(world, pos.offset(pushFacing), world.getBlockState(pos.offset(pushFacing)), pushFacing)) {
            push(world, pos, pos.offset(pushFacing), pushFacing);
            //world.setBlockState(pos.offset(pushFacing),getDefaultState());
            return 1;
        } else {
            boolean done = false;
            if (canPushInto(world, pos.down(), world.getBlockState(pos.down()), EnumFacing.DOWN)) {
                push(world, pos, pos.down(), EnumFacing.DOWN);
                done = true;
            }
            if (!done)
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (canPushInto(world, pos.offset(facing), world.getBlockState(pos.offset(facing)), facing)) {
                        push(world, pos, pos.offset(facing), facing);
                        done = true;
                        break;
                    }
                }
            if (!done && canPushInto(world, pos.up(), world.getBlockState(pos.up()), EnumFacing.UP)) {
                push(world, pos, pos.up(), EnumFacing.UP);
                done = true;
            }
            if (!done) { //Explode
                explode(world, pos, pushFacing);
            }
            return 10;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
