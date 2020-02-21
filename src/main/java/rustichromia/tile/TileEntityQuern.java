package rustichromia.tile;

import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustichromia.block.BlockQuern;
import rustichromia.recipe.QuernRecipe;
import rustichromia.recipe.RecipeRegistry;
import rustichromia.util.ConsumerMechCapability;
import rustichromia.util.IHasSize;
import rustichromia.util.ItemBuffer;
import rustichromia.util.ItemStackHandlerUnique;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TileEntityQuern extends TileEntityBasicMachine<QuernRecipe> {
    ItemStackHandlerUnique inventory = new ItemStackHandlerUnique(new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    });

    ItemBuffer outputs = new ItemBuffer(this);

    public TileEntityQuern() {
        mechPower = new ConsumerMechCapability() {
            @Override
            public void onPowerChange() {
                TileEntityQuern.this.markDirty();
            }
        };
    }

    public EnumFacing getFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        return state.getValue(BlockQuern.facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        if(capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == EnumFacing.UP)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventory;
        if(capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == EnumFacing.UP)
            return (T) mechPower;
        return super.getCapability(capability, facing);
    }

    private void ejectItems() {
        if(outputs.isEmpty())
            return;
        ItemStack stack = outputs.removeFirst();
        if(!stack.isEmpty()) {
            EnumFacing facing = getFacing();

            if(hasInventory(facing))
                outputs.addFirst(pushToInventory(stack, facing, false));
            else
                dropItem(stack, facing);
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
    public void update() {
        super.update();
        if(!world.isRemote) {
            ejectItems();
        }
    }

    @Override
    public QuernRecipe findRecipe(double speed) {
        return RecipeRegistry.getQuernRecipe(this, speed, getCraftingItems());
    }

    @Override
    public boolean matchesRecipe(QuernRecipe recipe, double speed) {
        return recipe.matches(this, speed, getCraftingItems());
    }

    @Override
    public void consumeInputs(QuernRecipe recipe) {
        for (Ingredient ingredient : recipe.inputs) {
            consumeItem(ingredient);
        }
    }

    private void consumeItem(Ingredient ingredient) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if(ingredient.apply(stack)) {
                int amount = ingredient instanceof IHasSize ? ((IHasSize) ingredient).getSize() : 1;
                inventory.extractItem(i, amount, false);
            }
        }
    }

    @Override
    public void produceOutputs(QuernRecipe recipe, double speed) {
        List<ItemStack> results = recipe.getResults(this, speed, getCraftingItems());
        outputs.addAll(results);
    }

    @Override
    public void clearInventory() {
        World world = getWorld();
        BlockPos pos = getPos();
        rustichromia.util.Misc.dropInventory(world,pos,inventory);
        rustichromia.util.Misc.dropInventory(world,pos,outputs);
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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory",inventory.serializeNBT());
        mechPower.writeToNBT(compound);
        compound.setTag("outputs", outputs.serializeNBT());
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
