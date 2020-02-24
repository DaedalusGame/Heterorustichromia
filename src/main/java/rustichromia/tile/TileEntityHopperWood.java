package rustichromia.tile;

import mysticalmechanics.api.IMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustichromia.block.BlockHopperWood;
import rustichromia.util.ConsumerMechCapability;
import rustichromia.util.Misc;

import javax.annotation.Nullable;

public class TileEntityHopperWood extends TileEntity implements ITickable {
    public ItemStackHandler inventory = new ItemStackHandler(1);
    public IMechCapability mechPower;
    public double angle, lastAngle;
    public double power;

    public TileEntityHopperWood() {
        mechPower = new ConsumerMechCapability() {
            @Override
            public void onPowerChange() {
                TileEntityHopperWood.this.markDirty();
            }
        };
    }

    public EnumFacing getInputFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        if(state.getValue(BlockHopperWood.straight))
            return state.getValue(BlockHopperWood.facing).getOpposite();
        return EnumFacing.UP;
    }

    public EnumFacing getOutputFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        return state.getValue(BlockHopperWood.facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        if(capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing != getInputFacing() && facing != getOutputFacing())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventory;
        if(capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing != getInputFacing() && facing != getOutputFacing())
            return (T) mechPower;
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        double speed = mechPower.getPower(null);
        if(world.isRemote) {
            lastAngle = angle;
            angle += speed;
        } else {
            power += speed;
            while(power > 20) {
                processItems(1);
                power -= 20;
            }
        }
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        if(hand == EnumHand.OFF_HAND)
            return false;

        ItemStack heldItem = player.getHeldItem(hand);
        if(player.isSneaking() && heldItem.isEmpty())
        {
            world.setBlockState(pos,state.cycleProperty(BlockHopperWood.straight));
            return true;
        }

        return false;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        clearInventory();
    }

    public void clearInventory() {
        World world = getWorld();
        BlockPos pos = getPos();
        rustichromia.util.Misc.dropInventory(world,pos,inventory);
    }

    private ItemStack insertItem(ItemStack stack, IItemHandler target) {
        for(int j = 0; j < target.getSlots(); j++) {
            stack = target.insertItem(j,stack,false);
        }
        return stack;
    }

    private void processItems(int takeCount) {
        EnumFacing inputFacing = getInputFacing();
        EnumFacing outputFacing = getOutputFacing();
        TileEntity inputTile = getWorld().getTileEntity(getPos().offset(inputFacing));
        TileEntity outputTile = getWorld().getTileEntity(getPos().offset(outputFacing));
        if(inputTile != null && inputTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,inputFacing.getOpposite())) {
            IItemHandler inputHandler = inputTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,inputFacing.getOpposite());
            for (int j = 0; j < inputHandler.getSlots(); j++) {
                ItemStack extracted = inputHandler.extractItem(j, takeCount, true);
                if (!extracted.isEmpty()) {
                    ItemStack rest = insertItem(extracted.copy(), inventory);
                    if(rest.getCount() < extracted.getCount())
                    {
                        inputHandler.extractItem(j, extracted.getCount() - rest.getCount(), false);
                        break;
                    }
                }
            }
        }
        if(outputTile != null && outputTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,outputFacing.getOpposite())) {
            IItemHandler outputHandler = outputTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,outputFacing.getOpposite());
            for (int j = 0; j < inventory.getSlots(); j++) {
                ItemStack extracted = inventory.extractItem(j, takeCount, true);
                if (!extracted.isEmpty()) {
                    ItemStack rest = insertItem(extracted.copy(), outputHandler);
                    if(rest.getCount() < extracted.getCount())
                    {
                        inventory.extractItem(j, extracted.getCount() - rest.getCount(), false);
                        break;
                    }
                }
            }
        }
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
        power = compound.getDouble("power");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inventory",inventory.serializeNBT());
        mechPower.writeToNBT(compound);
        compound.setDouble("power",power);
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
