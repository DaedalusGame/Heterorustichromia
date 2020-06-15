package rustichromia.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustichromia.block.BlockCart;
import rustichromia.cart.*;
import rustichromia.cart.content.ContentItems;
import rustichromia.util.ItemStackHandlerCombined;
import rustichromia.util.Misc;
import rustichromia.util.Rotation;

import javax.annotation.Nullable;

public class TileEntityCart extends TileEntity implements ITickable {
    CartData data;
    CartDataClient clientData;

    ItemStackHandler fuel;
    ItemStackHandlerCombined items;

    public TileEntityCart() {
        fuel = new ItemStackHandler(1);
        items = new ItemStackHandlerCombined() {
            @Override
            public IItemHandler getFirst() {
                CartContent content = getContent();
                if(isStopped() && content instanceof ContentItems)
                    return ((ContentItems) content).getItemHandler();
                return null;
            }

            @Override
            public IItemHandler getSecond() {
                return fuel;
            }
        };
    }

    public boolean isStopped() {
        ICartData data = getData();
        if(data != null)
            return data.isState(CartState.STOP);
        return false;
    }

    @Nullable
    private ICartData getData() {
        if(world.isRemote)
            return clientData;
        else
            return data;
    }

    private CartContent getContent() {
        ICartData data = getData();
        if(data != null)
        return data.getContent();
        return null;
    }

    public void setData(CartData data) {
        this.data = data;
        this.data.setTile(this);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if(data != null) {
            CartData.remove(data);
        }
    }

    @Override
    public void update() {
        if(!world.isRemote) {
            if (data != null && !data.isDestroyed()) {
                if (data.uuid == null)
                    CartData.add(data);
                if (data.tile == null)
                    data.setTile(this);
                if (data.tile != this) { //ruh roh bad touch, obliterate self
                    data = null;
                    world.setBlockToAir(pos);
                    return;
                }

                data.update();
                markDirty();
            } else {
                world.setBlockToAir(pos);
            }
        } else if(clientData != null && !clientData.isDestroyed()) {
            clientData.update(this);
        }
    }

    public void move(BlockPos newPos) {
        BlockPos pos = getPos();

        if(newPos.equals(pos))
            return;

        IBlockState state = world.getBlockState(pos);

        if(canMove(newPos)) {
            BlockCart.setMoving(true);
            world.setBlockState(newPos, state);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            TileEntityCart newTile = (TileEntityCart) world.getTileEntity(newPos);
            if(newTile != null) {
                NBTTagCompound nbt = serializeNBT();
                //Expunge position data or bad things happen
                nbt.setInteger("x",newPos.getX());
                nbt.setInteger("y",newPos.getY());
                nbt.setInteger("z",newPos.getZ());
                newTile.deserializeNBT(nbt);
                newTile.setData(data);
            }
            BlockCart.setMoving(false);
        }
    }

    public boolean canMove(BlockPos newPos) {
        IBlockState state = world.getBlockState(pos);
        return world.isValid(newPos) && state.getBlock().canPlaceBlockAt(world,newPos);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        CartContent content = getContent();
        if(isStopped() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        if(isStopped() && content != null && content.hasCapability(capability, facing))
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        CartContent content = getContent();
        if(isStopped() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) items;
        if(isStopped() && content != null) {
            T cap = content.getCapability(capability, facing);
            if(cap != null)
                return cap;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
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
        NBTTagCompound compound = super.getUpdateTag();
        compound.setLong("uuidMost", data.uuid.getMostSignificantBits());
        compound.setLong("uuidLeast", data.uuid.getLeastSignificantBits());
        compound.setTag("state", data.state.writeToNBT(new NBTTagCompound()));
        if(data.content != null)
            compound.setTag("content", data.content.serialize());
        compound.setTag("move", data.moveMachine.writeToNBT(new NBTTagCompound()));
        compound.setTag("turn", data.turnMachine.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.getNbtCompound();
        readFromNBT(nbt);
        clientData = CartDataClient.getOrCreate(nbt);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this, false);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if(data != null)
            compound.setTag("cartData", data.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("cartData"))
            data = new CartData(compound.getCompoundTag("cartData"));
    }

    private NBTTagCompound writeVec3d(Vec3d vec3d) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("x",vec3d.x);
        compound.setDouble("y",vec3d.y);
        compound.setDouble("z",vec3d.z);
        return compound;
    }

    private Vec3d readVec3d(NBTTagCompound compound) {
        return new Vec3d(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
    }

    private NBTTagCompound writeRotation(Rotation rotation) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setFloat("x",rotation.x);
        compound.setFloat("y",rotation.y);
        compound.setFloat("z",rotation.z);
        return compound;
    }

    private Rotation readRotation(NBTTagCompound compound) {
        return new Rotation(compound.getFloat("x"), compound.getFloat("y"), compound.getFloat("z"));
    }
}
