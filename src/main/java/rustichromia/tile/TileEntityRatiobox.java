package rustichromia.tile;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.IHasRotation;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.Misc;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import rustichromia.Registry;
import rustichromia.Rustichromia;
import rustichromia.block.BlockRatiobox;
import rustichromia.gui.GuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityRatiobox extends TileEntity implements ITickable, IHasRotation {
    protected boolean isBroken;

    private EnumFacing sideA, sideB;
    private double ratioOff, ratioOn;
    private boolean active;

    public double inputAngle, inputLastAngle;
    public double aAngle, aLastAngle;
    public double bAngle, bLastAngle;

    public DefaultMechCapability capability;

    public TileEntityRatiobox() {
        super();
        capability = createCapability();
    }

    public DefaultMechCapability createCapability() {
        return new RatioboxMechCapability();
    }

    public void updateNeighbors() {
        IBlockState state = world.getBlockState(getPos());
        EnumFacing input = getInput();

        //sets Gearbox Input.
        if (state.getBlock() instanceof BlockRatiobox) {
            TileEntity t = world.getTileEntity(getPos().offset(input));
            if (t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, input.getOpposite()) && capability.isInput(input)) {
                if(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, input.getOpposite()).isOutput(input.getOpposite())) {
                    capability.setPower(t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, input.getOpposite()).getPower(input.getOpposite()), input);
                }else if(capability.isInput(input)) {
                    capability.setPower(0, input);
                }
            }
        }

        //Manages Power Output.
        for (EnumFacing f : EnumFacing.values()) {
            BlockPos p = getPos().offset(f);
            TileEntity t = world.getTileEntity(p);
            if(!capability.isInput(f) && t != null && t.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite())) {
                t.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, f.getOpposite()).setPower(capability.getPower(f), f.getOpposite());
            }
        }
        markDirty();
    }

    public EnumFacing getInput() {
        IBlockState state = world.getBlockState(getPos());
        return state.getValue(BlockRatiobox.input);
    }

    public EnumFacing getSideA() {
        return sideA;
    }

    public EnumFacing getSideB() {
        return sideB;
    }

    private double getCurrentRatioA() {
        if(sideA != null && sideB != null)
            return active ? ratioOn : ratioOff;
        else
            return 1.0;
    }

    private double getCurrentRatioB() {
        if(sideA != null && sideB != null)
            return active ? 1 - ratioOn : 1 - ratioOff;
        else
            return 1.0;
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            double inSpeed = capability.getPower(getInput());
            boolean isRedstoneActive = world.isBlockIndirectlyGettingPowered(pos) > 0;
            if (isRedstoneActive != active) {
                active = isRedstoneActive;
                capability.onPowerChange();
            }
            if(inSpeed > 20 && world.rand.nextDouble() < 0.01 * (inSpeed - 20))
                breakRatiobox(false);
        } else {
            updateAngle();
        }
    }

    private void breakRatiobox(boolean dropItem) {
        World world = getWorld();
        BlockPos pos = getPos();
        IBlockState state = world.getBlockState(pos);
        breakBlock(world, pos, state,null);
        if(dropItem)
            state.getBlock().dropBlockAsItem(world, pos,state,0);
        else
            world.playEvent(2001, pos, Block.getStateId(state));
        world.setBlockToAir(pos);
        world.playSound(null,pos, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.BLOCKS, 1, 1);
    }


    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        isBroken = true;
        capability.setPower(0, null);
        updateNeighbors();
    }

    protected void updateAngle() {
        inputLastAngle = inputAngle;
        aLastAngle = aAngle;
        bLastAngle = bAngle;

        inputAngle += capability.getVisualPower(getInput());
        aAngle += capability.getVisualPower(sideA);
        bAngle += capability.getVisualPower(sideB);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (this.capability.isInput(facing) || this.capability.isOutput(facing))) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && (this.capability.isInput(facing) || this.capability.isOutput(facing))) {
            return MysticalMechanicsAPI.MECH_CAPABILITY.cast(this.capability);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        capability.readFromNBT(compound);
        ratioOff = compound.getDouble("ratioOff");
        ratioOn = compound.getDouble("ratioOn");
        sideA = toNullFacing(compound.getInteger("sideA"));
        sideB = toNullFacing(compound.getInteger("sideB"));
        active = compound.getBoolean("active");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        capability.writeToNBT(compound);
        compound.setDouble("ratioOff",ratioOff);
        compound.setDouble("ratioOn",ratioOn);
        compound.setInteger("sideA",fromNullFacing(sideA));
        compound.setInteger("sideB",fromNullFacing(sideB));
        compound.setBoolean("active",active);
        return compound;
    }

    private EnumFacing toNullFacing(int i) {
        if(i < 0)
            return null;
        else
            return EnumFacing.getFront(i);
    }

    private int fromNullFacing(EnumFacing facing) {
        if(facing == null)
            return -1;
        else
            return facing.getIndex();
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
        Misc.syncTE(this, isBroken);
    }

    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldStack = player.getHeldItem(hand);

        if(hand != EnumHand.MAIN_HAND)
            return false;

        if(heldStack.getItem() == Item.getItemFromBlock(Registry.AXLE_WOOD)) {
            if(facing != sideA && facing != sideB && facing != getInput()) {
                if(sideA == null)
                    sideA = facing;
                else if(sideB == null)
                    sideB = facing;
                capability.onPowerChange();
                return true;
            }
        } else if(heldStack.isEmpty()) {
            if(facing == sideA) {
                if(player.isSneaking())
                    sideA = null;
                else {
                    swapSides();
                }
                capability.onPowerChange();
            } else if(facing == sideB) {
                if(player.isSneaking())
                    sideB = null;
                else {
                    swapSides();
                }
                capability.onPowerChange();
            } /*else if(facing != getInput()) {
                if(player.isSneaking())
                    if(active)
                        ratioOn = Math.max(0,ratioOn-0.1);
                    else
                        ratioOff = Math.max(0,ratioOff-0.1);
                else {
                    if(active)
                        ratioOn = Math.min(1,ratioOn+0.1);
                    else
                        ratioOff = Math.min(1,ratioOff+0.1);
                }
            }*/ else {
                player.openGui(Rustichromia.MODID, GuiHandler.RATIOBOX, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }

        return false;
    }

    private void swapSides() {
        EnumFacing temp = sideA;
        sideA = sideB;
        sideB = temp;
    }

    @Override
    public boolean hasRotation(@Nonnull EnumFacing side) {
        if(side == sideA || side == sideB || side == getInput())
            return true;
        return false;
    }

    @Override
    public double getAngle(@Nonnull EnumFacing side) {
        if(side == sideA)
            return aAngle;
        else if(side == sideB)
            return bAngle;
        else if(side == getInput())
            return inputAngle;
        else
            return 0;
    }

    @Override
    public double getLastAngle(@Nonnull EnumFacing side) {
        if(side == sideA)
            return aLastAngle;
        else if(side == sideB)
            return bLastAngle;
        else if(side == getInput())
            return inputLastAngle;
        else
            return 0;
    }

    public double getRatioOff() {
        return ratioOff;
    }

    public double getRatioOn() {
        return ratioOn;
    }

    public void setRatio(double ratioOn, double ratioOff) {
        this.ratioOn = MathHelper.clamp(roundTo(ratioOn,100),0,1);
        this.ratioOff = MathHelper.clamp(roundTo(ratioOff, 100), 0,1);
        capability.onPowerChange();
    }

    private static double roundTo(double num, double n) {
        return Math.round(num * n) / n;
    }

    public boolean hasAxle(EnumFacing facing) {
        return facing == getInput() || facing == getSideA() || facing == getSideB();
    }

    private class RatioboxMechCapability extends DefaultMechCapability {
        @Override
        public void onPowerChange() {
            TileEntityRatiobox box = TileEntityRatiobox.this;
            box.updateNeighbors();
            box.markDirty();
        }

        @Override
        public double getPower(EnumFacing from) {
            if(isInput(from) || isOutput(from))
                return getInternalPower(from);
            return 0;
        }

        @Override
        public double getVisualPower(EnumFacing from) {
            if(isInput(from) || isOutput(from))
                return getInternalPower(from);
            return 0;
        }

        protected double getInternalPower(EnumFacing from) {

            if (isInput(from))
                return power;
            else if(sideA != null && from == sideA)
                return power * getCurrentRatioA();
            else if(sideB != null && from == sideB)
                return power * getCurrentRatioB();
            else
                return 0;
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            if(from == null) {
                this.power = 0;
                onPowerChange();
            }
            if (isInput(from) && !isBroken) {
                double oldPower = capability.power;
                if (oldPower != value) {
                    capability.power = value;
                    onPowerChange();
                }
            }
        }

        @Override
        public boolean isInput(EnumFacing from) {
            return getInput() == from;
        }

        @Override
        public boolean isOutput(EnumFacing from) {

            return (sideA != null && from == sideA) || (sideB != null && from == sideB);
        }
    }
}
