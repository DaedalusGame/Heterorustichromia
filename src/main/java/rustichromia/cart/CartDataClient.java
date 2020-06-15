package rustichromia.cart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Quaternion;
import rustichromia.entity.EntityCart;
import rustichromia.util.TurnMachine;

import java.util.HashMap;
import java.util.UUID;

public class CartDataClient implements ICartData {
    static HashMap<UUID, CartDataClient> cartMap = new HashMap<>();

    UUID uuid;
    EntityCart entity;

    boolean destroyed;

    CartState state;
    CartContent content;
    TurnMachine.MoveMachine moveMachine;
    TurnMachine.FloorMachine turnMachine;

    public Vec3d lastOffset = Vec3d.ZERO;
    public Vec3d offset = Vec3d.ZERO;

    public Quaternion lastRotation = new Quaternion(0, 0, 0, 0);
    public Quaternion rotation = new Quaternion(0, 0, 0, 0);

    public static CartDataClient getOrCreate(NBTTagCompound compound) {
        UUID uuid = new UUID(compound.getLong("uuidMost"), compound.getLong("uuidLeast"));
        CartDataClient data = cartMap.get(uuid);
        if (data == null) {
            data = new CartDataClient(uuid);
            cartMap.put(uuid, data);
        }
        data.readFromNBT(compound);
        return data;
    }

    public static void remove(UUID uuid) {
        CartDataClient data = cartMap.get(uuid);
        if(data != null) {
            data.setDead();
            cartMap.remove(uuid);
        }
    }

    public static void cleanup() {
        cartMap.clear();
    }

    public CartDataClient(UUID uuid) {
        this.uuid = uuid;
        setState(CartState.INVALID);
        this.moveMachine = new TurnMachine.MoveMachine(BlockPos.ORIGIN);
        this.turnMachine = new TurnMachine.FloorMachine(EnumFacing.NORTH, EnumFacing.UP);
    }

    public BlockPos getPos() {
        return moveMachine.getActiveValue();
    }

    @Override
    public CartContent getContent() {
        return content;
    }

    @Override
    public CartState getState() {
        return state;
    }

    @Override
    public boolean isState(CartStateSupplier supplier) {
        return state.getType().equals(supplier.getResourceLocation());
    }

    public void setState(CartStateSupplier supplier) {
        this.state = supplier.get();
    }

    @Override
    public boolean isIncoming() {
        return moveMachine.getBuildup() < 0;
    }

    @Override
    public boolean isStill() {
        return moveMachine.getBuildup() == 0;
    }

    @Override
    public boolean isOutgoing() {
        return moveMachine.getBuildup() > 0;
    }

    private void setDead() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void update(TileEntity tile) {
        if(entity == null || entity.isDead) {
            entity = new EntityCart(tile.getWorld());
            entity.setData(this);
            entity.forceSpawn = true;
            tile.getWorld().spawnEntity(entity);
        } else {
            BlockPos currentPos = moveMachine.getActiveValue();
            entity.setPositionAndUpdate(currentPos.getX() + 0.5,currentPos.getY() + 0,currentPos.getZ() + 0.5);
        }

        lastOffset = offset;
        offset = moveMachine.getOffset();

        lastRotation = rotation;
        rotation = turnMachine.getRotation();

        moveMachine.update(state.getMoveSpeed());
        turnMachine.update(state.getTurnSpeed());
    }

    private void readFromNBT(NBTTagCompound nbt) {
        state = CartState.deserialize(nbt.getCompoundTag("state"));
        content = CartContent.deserialize(nbt.getCompoundTag("content"));
        moveMachine.readFromNBT(nbt.getCompoundTag("move"));
        turnMachine.readFromNBT(nbt.getCompoundTag("turn"));
    }
}
