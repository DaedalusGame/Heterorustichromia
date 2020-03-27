package rustichromia.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustichromia.Rustichromia;

import java.util.List;

public class ResultBlock extends Result {
    public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Rustichromia.MODID, "block");

    static {
        register(RESOURCE_LOCATION, ResultBlock::new);
    }

    IBlockState state;
    ItemStack stack;
    boolean done;

    public ResultBlock() {
        this(Blocks.AIR.getDefaultState(), ItemStack.EMPTY);
    }

    public ResultBlock(IBlockState state){
        this(state, new ItemStack(state.getBlock(),1, state.getBlock().getMetaFromState(state)));
    }

    public ResultBlock(IBlockState state, ItemStack stack) {
        super(RESOURCE_LOCATION);
        this.state = state;
        this.stack = stack;
    }

    @Override
    public boolean isEmpty() {
        return state.getBlock() == Blocks.AIR || done;
    }

    @Override
    public Result transform() {
        return new ResultBlock(state,stack);
    }

    @Override
    public void drop(World world, BlockPos pos) {
        //NOOP
    }

    @Override
    public void output(ItemBuffer buffer) {
        done = buffer.ejectBlock(state);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        compound.setString("blockType", block.getRegistryName().toString());
        compound.setInteger("blockMeta", meta);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(compound.getString("blockType")));
        int meta = compound.getInteger("blockMeta");
        state = block.getStateFromMeta(meta);
    }

    @Override
    public ItemStack getJEIStack() {
        return stack;
    }

    @Override
    public void getJEITooltip(List<String> tooltip) {
        tooltip.add(1, I18n.format("rustichromia.tooltip.result.placeblock"));
    }
}
