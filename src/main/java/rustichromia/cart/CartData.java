package rustichromia.cart;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import rustichromia.cart.content.ContentItems;
import rustichromia.network.MessageCartCleanup;
import rustichromia.network.PacketHandler;
import rustichromia.tile.TileEntityCart;
import rustichromia.tile.TileEntityCartControl;
import rustichromia.util.CartUtil;
import rustichromia.util.Misc;
import rustichromia.util.TurnMachine;

import java.util.*;

public class CartData implements ICartData {
    static Random random = new Random();

    static List<CartData> carts = new ArrayList<>();
    static HashMap<UUID, CartData> cartMap = new HashMap<>();

    public UUID uuid;
    public TileEntityCart tile;
    public CartState state;
    public CartContent content;

    public boolean destroyed;

    public TurnMachine.MoveMachine moveMachine;
    public TurnMachine.FloorMachine turnMachine;

    public static void add(CartData cartData) {
        while(cartData.uuid == null || (cartMap.containsKey(cartData.uuid) && cartMap.get(cartData.uuid) != cartData)) //Redo cart uuid until it doesn't collide (approx O(1))
            cartData.uuid = MathHelper.getRandomUUID(random);
        carts.add(cartData);
        cartMap.put(cartData.uuid, cartData);
    }

    public static CartData create(BlockPos pos, EnumFacing forward, EnumFacing up) {
        CartData cart = new CartData(pos, forward, up);
        cart.setContent(new ContentItems(ContentItems.SLOTS));
        add(cart);
        return cart;
    }

    public static CartData find(UUID uuid) {
        return cartMap.get(uuid);
    }

    public static void remove(CartData data) {
        if(data.uuid != null) {
            carts.remove(data);
            cartMap.remove(data.uuid);
            data.setDestroyed();
            PacketHandler.INSTANCE.sendToAll(new MessageCartCleanup(data.uuid));
        }
    }

    public static void cleanup() {
        carts.clear();
        cartMap.clear();
    }

    public CartData(BlockPos pos, EnumFacing forward, EnumFacing up) {
        setState(CartState.MOVE);
        moveMachine = new TurnMachine.MoveMachine(pos) {
            @Override
            public void onChange() {
                tryMove();
            }
        };
        turnMachine = new TurnMachine.FloorMachine(forward, up);
    }

    public CartData(NBTTagCompound compound) {
        this(BlockPos.ORIGIN, EnumFacing.NORTH, EnumFacing.UP);
        readFromNBT(compound);
    }

    public TileEntityCart getTile() {
        return tile;
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
    public CartContent getContent() {
        return content;
    }

    public void setContent(CartContent content) {
        this.content = content;
    }

    public void trySetContent(CartContent content) {
        if(this.content.isEmpty())
            this.content = content;
    }

    public EnumFacing getForward() {
        return turnMachine.getForward();
    }

    public EnumFacing getUp() {
        return turnMachine.getUp();
    }

    public EnumFacing getDown() {
        return turnMachine.getUp().getOpposite();
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

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed() {
        this.destroyed = true;
    }

    public void setTile(TileEntityCart tile) {
        this.tile = tile;
    }

    private void tryMove() {
        BlockPos newPos = moveMachine.getCurrentValue();

        if(tile.canMove(newPos)) {
            tile.move(newPos);
            moveMachine.finishMove(true);
        } else {
            moveMachine.finishMove(false);
        }
    }

    public void update() {
        state.update();
        content.update();

        moveMachine.update(state.getMoveSpeed());
        turnMachine.update(state.getTurnSpeed());

        World world = tile.getWorld();
        EnumFacing forward = getForward();
        EnumFacing down = getDown();

        BlockPos pos = tile.getPos();
        BlockPos posWall = pos.offset(forward);
        BlockPos posFloor = pos.offset(down);
        BlockPos posNextFloor = posWall.offset(down);

        List<TileEntityCartControl> controls = CartUtil.getControls(world, posFloor);

        boolean solidWall = isWall(world, posWall);
        boolean solidFloor = isFloor(world, posFloor);
        boolean solidNextFloor = isFloor(world, posNextFloor);

        if(controls.size() == 1) {
            TileEntityCartControl tileControl = controls.get(0);
            Control control = tileControl.getControl();
            if(control.isActiveSide(tileControl, getUp()))
                control.controlCart(tileControl, this);
        } else {
            if(!solidFloor) { //Obliterate

            } else if(solidNextFloor && !solidWall) { //Move ahead
                if(moveMachine.canChange() && turnMachine.canChange()) {
                    moveMachine.setNext(posWall);
                }
            }
        }
        /*if(!solidNextFloor && !solidWall) {
            if(moveMachine.canChange() && turnMachine.canChange()) {
                moveMachine.setNext(posNextFloor);
                turnMachine.setNext(TurnMachine.EnumTurn.TURN_DOWN);
            }
        } else if(solidWall) {
            if(turnMachine.canChange()) {
                turnMachine.setNext(TurnMachine.EnumTurn.TURN_UP);
            }
        } else if(solidNextFloor) {
            if(moveMachine.canChange()) {
                moveMachine.setNext(posWall);
            }
        }*/
    }

    public void orientAndMove(EnumFacing facing) {
        EnumFacing forward = getForward();

        orient(facing);
        if(facing != forward)
            return;
        moveForward();
    }

    public void orient(EnumFacing facing) {
        if(!moveMachine.canChange() || !turnMachine.canChange())
            return;

        EnumFacing forward = getForward();
        EnumFacing up = getUp();

        if(facing == forward)
            return;

        int horizontalInversion = up.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1 : -1;
        EnumFacing down = up.getOpposite();
        EnumFacing left = Misc.turn(forward, up.getAxis(),-1 * horizontalInversion);
        EnumFacing right = Misc.turn(forward, up.getAxis(),1 * horizontalInversion);
        EnumFacing backward = forward.getOpposite();

        if(facing == up) {
            turnMachine.setNext(TurnMachine.EnumTurn.TURN_UP);
        } else if(facing == down) {
            turnMachine.setNext(TurnMachine.EnumTurn.TURN_DOWN);
        } else if(facing == left) {
            turnMachine.setNext(TurnMachine.EnumTurn.TURN_LEFT);
        } else if(facing == right) {
            turnMachine.setNext(TurnMachine.EnumTurn.TURN_RIGHT);
        } else if(facing == backward) {
            turnMachine.setNext(TurnMachine.EnumTurn.TURN_LEFT_AROUND);
        }
    }

    public void moveForward() {
        if(!moveMachine.canChange() || !turnMachine.canChange())
            return;

        World world = tile.getWorld();

        EnumFacing forward = getForward();
        EnumFacing down = getDown();

        BlockPos pos = tile.getPos();
        BlockPos posWall = pos.offset(forward);
        BlockPos posNextFloor = posWall.offset(down);

        boolean solidWall = isWall(world, posWall);
        boolean solidNextFloor = isFloor(world, posNextFloor);

        if(solidNextFloor && !solidWall)
            moveMachine.setNext(pos.offset(forward));
    }

    public void moveRail() {
        if(hasControlAhead()) {
            moveForward();
        }
    }

    private boolean isFloor(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlockFaceShape(world, pos, getUp()) == BlockFaceShape.SOLID;
    }

    private boolean isWall(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return !state.getBlock().isReplaceable(world, pos);
    }

    public boolean hasControlAhead() {
        World world = tile.getWorld();
        EnumFacing forward = getForward();
        EnumFacing down = getDown();

        BlockPos posWall = tile.getPos().offset(forward);
        BlockPos posNextFloor = posWall.offset(down);

        if(CartUtil.hasControl(world, posNextFloor))
            return true;
        return false;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        saveUUID(nbt, uuid);
        nbt.setTag("state",state.writeToNBT(new NBTTagCompound()));
        if(content != null)
            nbt.setTag("content",content.serialize());
        nbt.setTag("move",moveMachine.writeToNBT(new NBTTagCompound()));
        nbt.setTag("turn",turnMachine.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        uuid = loadUUID(nbt);
        state = CartState.deserialize(nbt.getCompoundTag("state"));
        content = CartContent.deserialize(nbt.getCompoundTag("content"));
        content.setCart(this);
        moveMachine.readFromNBT(nbt.getCompoundTag("move"));
        turnMachine.readFromNBT(nbt.getCompoundTag("turn"));
    }

    public static UUID loadUUID(NBTTagCompound compound) {
        return new UUID(compound.getLong("uuidMost"),compound.getLong("uuidLeast"));
    }

    public static void saveUUID(NBTTagCompound compound, UUID uuid) {
        compound.setLong("uuidMost",uuid.getMostSignificantBits());
        compound.setLong("uuidLeast",uuid.getLeastSignificantBits());
    }
}
