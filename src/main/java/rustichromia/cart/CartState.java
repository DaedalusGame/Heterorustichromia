package rustichromia.cart;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import rustichromia.Rustichromia;

import java.util.function.Supplier;

//TODO: allow subclasses (add serializer etc etc)
public class CartState {
    public static final CartStateSupplier INVALID = new CartStateSupplier(new ResourceLocation(Rustichromia.MODID, "invalid")) {
        @Override
        public CartState get() {
            return new CartState(resourceLocation, 0, 0);
        }
    };
    public static final CartStateSupplier MOVE = new CartStateSupplier(new ResourceLocation(Rustichromia.MODID, "move")) {
        @Override
        public CartState get() {
            return new CartState(resourceLocation, 0.1, 0.1);
        }
    };
    public static final CartStateSupplier STOPPING = new CartStateSupplier(new ResourceLocation(Rustichromia.MODID, "stopping")) {
        @Override
        public CartState get() {
            return new CartState(resourceLocation, 0.1, 0.1);
        }
    };
    public static final CartStateSupplier STOP = new CartStateSupplier(new ResourceLocation(Rustichromia.MODID, "stop")) {
        @Override
        public CartState get() {
            return new CartState(resourceLocation, 0, 0.1);
        }
    };

    public static CartState deserialize(NBTTagCompound compound) {
        ResourceLocation resLoc = new ResourceLocation(compound.getString("type"));
        try {
            Supplier<CartState> supplier = CartState::new;
            CartState result = supplier.get().readFromNBT(compound);
            return result;
        } catch (Exception e) {
            System.out.println("Failed to deserialize cart state '"+resLoc+"'");
        }
        return null;
    }

    ResourceLocation resourceLocation;
    double moveSpeed;
    double turnSpeed;
    int time;

    public CartState() { }

    public CartState(ResourceLocation resourceLocation, double moveSpeed, double turnSpeed) {
        this.resourceLocation = resourceLocation;
        this.moveSpeed = moveSpeed;
        this.turnSpeed = turnSpeed;
    }

    public ResourceLocation getType() {
        return resourceLocation;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public double getTurnSpeed() {
        return turnSpeed;
    }

    public int getTime() {
        return time;
    }

    public void update() {
        time++;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setString("type",resourceLocation.toString());
        nbt.setDouble("moveSpeed",moveSpeed);
        nbt.setDouble("turnSpeed",turnSpeed);
        nbt.setInteger("time", time);
        return nbt;
    }

    public CartState readFromNBT(NBTTagCompound nbt) {
        resourceLocation = new ResourceLocation(nbt.getString("type"));
        moveSpeed = nbt.getDouble("moveSpeed");
        turnSpeed = nbt.getDouble("turnSpeed");
        time = nbt.getInteger("time");
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CartState) {
            return ((CartState) obj).getType().equals(getType());
        }
        return super.equals(obj);
    }
}
