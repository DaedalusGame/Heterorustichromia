package rustichromia.tile;

import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.handler.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;
import rustichromia.Registry;
import rustichromia.block.BlockHayCompactor;
import rustichromia.block.BlockHayCompactor.EnumType;
import rustichromia.block.MultiBlockPart;
import rustichromia.recipe.HayCompactorRecipe;
import rustichromia.recipe.RecipeRegistry;
import rustichromia.util.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileEntityHayCompactor extends TileEntityBasicMachine<HayCompactorRecipe> implements IMultiTile, ITickable {
    MultiBlockPart part = new MultiBlockPart(0,0,0);
    boolean multiBlockValid;

    ItemStackHandlerUnique inventory = new ItemStackHandlerUnique(new ItemStackHandler(3){
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    });
    ItemBuffer outputs = new ItemBuffer(this) {
        @Override
        public ItemStack ejectItem(ItemStack stack) {
            return TileEntityHayCompactor.this.ejectItem(stack);
        }

        @Override
        public boolean ejectBlock(IBlockState state) {
            return TileEntityHayCompactor.this.ejectBlock(state);
        }
    };

    public TileEntityHayCompactor() {
        mechPower = new ConsumerMechCapability() {
            @Override
            public void onPowerChange() {
                TileEntityHayCompactor.this.markDirty();
            }
        };
    }

    public boolean isMultiBlockValid() {
        return multiBlockValid;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing != null && facing.getAxis() != EnumFacing.Axis.Y)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing != null && facing.getAxis() != EnumFacing.Axis.Y)
            return (T) mechPower;
        return super.getCapability(capability, facing);
    }

    @Override
    public HayCompactorRecipe findRecipe(double speed) {
        return RecipeRegistry.getHayCompactorRecipe(this, speed, getCraftingItems());
    }

    @Override
    public boolean matchesRecipe(HayCompactorRecipe recipe, double speed) {
        return recipe.matches(this, speed, getCraftingItems());
    }

    @Override
    public void consumeInputs(HayCompactorRecipe recipe) {
        for (Ingredient ingredient : recipe.inputs) {
            consumeItem(ingredient);
        }
    }

    private void consumeItem(Ingredient ingredient) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (ingredient.apply(stack)) {
                int amount = ingredient instanceof IHasSize ? ((IHasSize) ingredient).getSize() : 1;
                inventory.extractItem(i, amount, false);
            }
        }
    }

    @Override
    public void produceOutputs(HayCompactorRecipe recipe, double speed) {
        List<Result> results = recipe.getResults(this, speed, getCraftingItems());
        outputs.addAll(results);
    }

    @Override
    public void clearInventory() {
        World world = getWorld();
        BlockPos pos = getPos();
        Misc.dropInventory(world,pos,inventory);
        Misc.dropInventory(world,pos,outputs);
    }

    private List<ItemStack> getCraftingItems() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); i++)
            stacks.add(inventory.getStackInSlot(i));
        return stacks;
    }

    private void ejectResults() {
        if (outputs.isEmpty())
            return;
        Result result = outputs.removeFirst();
        if (!result.isEmpty()) {
            result.output(outputs);
            outputs.addFirst(result);
        }
    }

    private boolean ejectBlock(IBlockState state) {
        EnumFacing facing = EnumFacing.DOWN;
        BlockPos checkPos = pos.offset(facing);
        IBlockState checkState = world.getBlockState(checkPos);
        if(checkState.getBlock().isReplaceable(world, checkPos)) {
            world.setBlockState(checkPos, state);
            return true;
        }
        return false;
    }

    private ItemStack ejectItem(ItemStack stack) {
        EnumFacing facing = EnumFacing.DOWN;
        if(hasInventory(facing)) {
            ItemStack remainder = pushToInventory(stack, facing, false);
            return remainder;
        } else {
            dropItem(stack);
            return ItemStack.EMPTY;
        }
    }

    private void dropItem(ItemStack stack) {
        EntityItem item = new EntityItem(getWorld(), getPos().getX() + 0.5f, getPos().getY() - 0.4f, getPos().getZ() + 0.5f, stack);
        item.motionX = 0;
        item.motionY = -0.1f;
        item.motionZ = 0;
        getWorld().spawnEntity(item);
    }

    @Override
    public void update() {
        super.update();
        if (!world.isRemote) {
            ejectResults();
        }
    }

    public void initPart(int x, int y, int z) {
        //NOOP
    }

    @Override
    public MultiBlockPart getPart() {
        return part;
    }

    @Override
    public void build() {
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                for (int y = 0; y <= 2; y++)
                {
                    if(y == 0 && (x != 0 || z != 0))
                        continue;
                    EnumType type = EnumType.None;
                    if(x == 0 && z == 0 && y == 2)
                        type = EnumType.Inlet;
                    BlockPos buildPos = getPos().add(x,y,z);
                    IBlockState state = world.getBlockState(buildPos);
                    if(state.getBlock().isReplaceable(world,buildPos)) {
                        world.setBlockState(buildPos, Registry.HAY_COMPACTOR.getDefaultState().withProperty(BlockHayCompactor.TYPE,type));
                        TileEntity tile = world.getTileEntity(buildPos);
                        if(tile instanceof IMultiSlave)
                            ((IMultiSlave) tile).initPart(x,y,z);
                    }
                }
            }
        }
        multiBlockValid = true;
    }

    @Override
    public void destroy(@Nullable BlockPos leak) {
        multiBlockValid = false;
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                for (int y = 0; y <= 2; y++)
                {
                    if(y == 0 && (x != 0 || z != 0))
                        continue;
                    BlockPos destroyPos = getPos().add(x,y,z);
                    IBlockState state = world.getBlockState(destroyPos);
                    Block block = state.getBlock();
                    if(block instanceof BlockHayCompactor)
                        ((BlockHayCompactor) block).breakPart(world,destroyPos);
                }
            }
        }
    }

    @Override
    public boolean isPartValid(BlockPos pos) {
        if (!isInBounds(pos))
            return true;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof BlockHayCompactor))
            return false;
        MultiBlockPart part = ((BlockHayCompactor) block).getPart(world,pos);
        if(part == null)
            return false;
        boolean valid = pos.add(part.getMasterOffset()).equals(getPos());
        return valid;
    }

    private boolean isInBounds(BlockPos pos) {
        int dx = pos.getX() - getPos().getX();
        int dy = pos.getY() - getPos().getY();
        int dz = pos.getZ() - getPos().getZ();
        if(Math.abs(dx) > 1 || Math.abs(dz) > 1)
            return false;
        if(dy < 0 || dy > 2)
            return false;
        if(dy == 0 && (dx != 0 || dz != 0))
            return false;
        return true;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        deserializeNBT(tag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this, false);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean("multiBlockValid", multiBlockValid);
        compound.setTag("inventory", inventory.serializeNBT());
        mechPower.writeToNBT(compound);
        compound.setTag("outputs", outputs.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        multiBlockValid = compound.getBoolean("multiBlockValid");
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        mechPower.readFromNBT(compound);
        outputs.deserializeNBT(compound.getTagList("outputs", 10));
        super.readFromNBT(compound);
    }

    public ItemStack getDisplayGear() {
        return new ItemStack(RegistryHandler.IRON_GEAR);
    }
}
