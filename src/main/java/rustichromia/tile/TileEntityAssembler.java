package rustichromia.tile;

import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustichromia.Rustichromia;
import rustichromia.block.BlockAssembler;
import rustichromia.block.BlockQuern;
import rustichromia.gui.GuiHandler;
import rustichromia.recipe.AssemblerRecipe;
import rustichromia.recipe.RecipeRegistry;
import rustichromia.util.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TileEntityAssembler extends TileEntityBasicMachine<AssemblerRecipe> {
    ItemStackHandlerUnique inventory;

    ItemBuffer outputs = new ItemBuffer(this) {
        @Override
        public ItemStack ejectItem(ItemStack stack) {
            return TileEntityAssembler.this.ejectItem(stack);
        }

        @Override
        public boolean ejectBlock(IBlockState state) {
            return TileEntityAssembler.this.ejectBlock(state);
        }
    };
    ResourceLocation filter = null;
    float itemAngle, lastItemAngle;

    public TileEntityAssembler() {
        this(0);
    }

    public TileEntityAssembler(int slots) {
        mechPower = new ConsumerMechCapability() {
            @Override
            public void onPowerChange() {
                TileEntityAssembler.this.markDirty();
            }
        };
        inventory = new ItemStackHandlerUnique(new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        });
    }

    public void setFilter(ResourceLocation id) {
        filter = id;
        markDirty();
    }

    public ResourceLocation getFilter() {
        return filter;
    }

    public EnumFacing getFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        return state.getValue(BlockQuern.facing);
    }

    public int getTier() {
        Block block = getBlockType();
        if (block instanceof BlockAssembler) {
            return ((BlockAssembler) block).getTier();
        }
        return 0;
    }

    public ItemStack getDisplayGear() {
        Block block = getBlockType();
        if (block instanceof BlockAssembler) {
            return ((BlockAssembler) block).getDisplayGear();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing != null && facing.getAxis() != EnumFacing.Axis.Y && facing != getFacing())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventory;
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing != null && facing.getAxis() != EnumFacing.Axis.Y && facing != getFacing())
            return (T) mechPower;
        return super.getCapability(capability, facing);
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
        EnumFacing facing = getFacing();
        BlockPos checkPos = pos.offset(facing);
        IBlockState checkState = world.getBlockState(checkPos);
        if(checkState.getBlock().isReplaceable(world, checkPos)) {
            world.setBlockState(checkPos, state);
            return true;
        }
        return false;
    }

    private ItemStack ejectItem(ItemStack stack) {
        EnumFacing facing = getFacing();
        if(hasInventory(facing)) {
            ItemStack remainder = pushToInventory(stack, facing, false);
            return remainder;
        } else {
            dropItem(stack, facing);
            return ItemStack.EMPTY;
        }
    }

    private void dropItem(ItemStack stack, EnumFacing facing) {
        EntityItem item = new EntityItem(getWorld(), getPos().getX() + 0.5f + facing.getFrontOffsetX() * 0.7f, getPos().getY() + 0.1f, getPos().getZ() + 0.5f + facing.getFrontOffsetZ() * 0.7f, stack);
        item.motionX = facing.getFrontOffsetX() * 0.4f;
        item.motionY = 0;
        item.motionZ = facing.getFrontOffsetZ() * 0.4f;
        getWorld().spawnEntity(item);
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        player.openGui(Rustichromia.MODID, GuiHandler.ASSEMBLER_RECIPE, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void update() {
        super.update();
        if (!world.isRemote) {
            ejectResults();
        } else {
            lastItemAngle = itemAngle;
            itemAngle += 1.5f;
        }
    }

    @Override
    public AssemblerRecipe findRecipe(double speed) {
        return RecipeRegistry.getAssemblerRecipe(this, getTier(), speed, getCraftingItems(), filter);
    }

    @Override
    public boolean matchesRecipe(AssemblerRecipe recipe, double speed) {
        if(filter != null && !recipe.id.equals(filter))
            return false;
        return recipe.matches(this, speed, getCraftingItems());
    }

    @Override
    public void consumeInputs(AssemblerRecipe recipe) {
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
    public void produceOutputs(AssemblerRecipe recipe, double speed) {
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

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        mechPower.readFromNBT(compound);
        outputs.deserializeNBT(compound.getTagList("outputs", 10));
        if(compound.hasKey("filter"))
            filter = new ResourceLocation(compound.getString("filter"));
        else
            filter = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        mechPower.writeToNBT(compound);
        compound.setTag("outputs", outputs.serializeNBT());
        if(filter != null)
            compound.setString("filter", filter.toString());
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
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
}
