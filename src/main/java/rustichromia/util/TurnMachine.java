package rustichromia.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Quaternion;
import sun.plugin.dom.exception.InvalidStateException;

public abstract class TurnMachine<T> {
    public static class Orientation {
        final EnumFacing up;
        final EnumFacing forward;

        public Orientation(EnumFacing up, EnumFacing forward) {
            this.up = up;
            this.forward = forward;
            checkGimbal();
        }

        public EnumFacing getUp() {
            return up;
        }

        public EnumFacing getForward() {
            return forward;
        }

        public Orientation rotate(EnumTurn turn)
        {
            EnumFacing.Axis hinge = getHinge();
            int horizontalInversion = up.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 1 : -1;
            int verticalInversion = forward.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? -1 : 1;
            int doubleInversion = horizontalInversion * verticalInversion;
            switch(turn) {
                case NONE:
                    return new Orientation(up,forward);
                case TURN_LEFT:
                    return new Orientation(up, Misc.turn(forward, up.getAxis(), -1 * horizontalInversion));
                case TURN_RIGHT:
                    return new Orientation(up, Misc.turn(forward, up.getAxis(), 1 * horizontalInversion));
                case TURN_LEFT_AROUND:
                    return new Orientation(up, Misc.turn(forward, up.getAxis(), -2 * horizontalInversion));
                case TURN_RIGHT_AROUND:
                    return new Orientation(up, Misc.turn(forward, up.getAxis(), 2 * horizontalInversion));
                case TURN_UP:
                    return new Orientation(forward.getOpposite(), up);
                case TURN_DOWN:
                    return new Orientation(forward, up.getOpposite());
            }
            assert false;
            return null;
        }

        private EnumFacing.Axis getHinge() {
            if((up.getAxis() == EnumFacing.Axis.X && forward.getAxis() == EnumFacing.Axis.Y) ||
                    (up.getAxis() == EnumFacing.Axis.Y && forward.getAxis() == EnumFacing.Axis.X))
                return EnumFacing.Axis.Z;
            else if((up.getAxis() == EnumFacing.Axis.X && forward.getAxis() == EnumFacing.Axis.Z) ||
                    (up.getAxis() == EnumFacing.Axis.Z && forward.getAxis() == EnumFacing.Axis.X))
                return EnumFacing.Axis.Y;
            else if((up.getAxis() == EnumFacing.Axis.Z && forward.getAxis() == EnumFacing.Axis.Y) ||
                    (up.getAxis() == EnumFacing.Axis.Y && forward.getAxis() == EnumFacing.Axis.Z))
                return EnumFacing.Axis.X;
            checkGimbal();
            assert false;
            return null;
        }

        private void checkGimbal() {
            if (forward.getAxis() == up.getAxis())
                throw new InvalidStateException("Turn machine is gimbally locked!");
        }
    }

    public enum EnumTurn implements IStringSerializable
    {
        NONE("none", 0,0),
        TURN_LEFT("turn_left",-90, 0),
        TURN_RIGHT("turn_right",90, 0),
        TURN_LEFT_AROUND("turn_left_around",-180, 0),
        TURN_RIGHT_AROUND("turn_right_around",180, 0),
        TURN_UP("turn_up",0, -90),
        TURN_DOWN("turn_down",0, 90);

        String name;
        double yaw, pitch;

        EnumTurn(String name, double yaw, double pitch) {
            this.name = name;
            this.yaw = yaw;
            this.pitch = pitch;
        }

        @Override
        public String getName() {
            return name;
        }

        public static EnumTurn byName(String name) {
            for (EnumTurn turn : EnumTurn.values()) {
                if(turn.getName() == name)
                    return turn;
            }
            return EnumTurn.NONE;
        }
    }

    public static class MoveMachine extends TurnMachine<BlockPos> {
        public MoveMachine(BlockPos pos) {
            super(pos);
        }

        public void finishMove(boolean success) {
            if(success) {

            } else { //BONK
                BlockPos h = lastValue;
                lastValue = currentValue;
                currentValue = h;
            }
        }

        public Vec3d getOffset() {
            BlockPos start = getStartValue();
            BlockPos end = getEndValue();
            double slide = buildup <= 0 ? buildup + 1 : buildup;
            return new Vec3d(
                    MathHelper.clampedLerp(start.getX(), end.getX(), slide),
                    MathHelper.clampedLerp(start.getY(), end.getY(), slide),
                    MathHelper.clampedLerp(start.getZ(), end.getZ(), slide)
            );
        }

        @Override
        protected NBTBase writeValue(BlockPos value) {
            if(value == null)
                return null;
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("x", value.getX());
            compound.setInteger("y", value.getY());
            compound.setInteger("z", value.getZ());
            return compound;
        }

        @Override
        protected BlockPos readValue(NBTBase nbt) {
            if(nbt == null)
                return null;
            NBTTagCompound compound = (NBTTagCompound) nbt;
            return new BlockPos(
                    compound.getInteger("x"),
                    compound.getInteger("y"),
                    compound.getInteger("z")
            );
        }
    }

    public static class FloorMachine extends TurnMachine<Orientation> {
        public FloorMachine(EnumFacing forward, EnumFacing up) {
            super(new Orientation(up, forward));
        }

        public EnumFacing getForward() {
            return getActiveValue().getForward();
        }

        public EnumFacing getUp() {
            return getActiveValue().getUp();
        }

        public void setNext(EnumTurn turn) {
            setNext(currentValue.rotate(turn));
        }

        public void reset(EnumFacing forward, EnumFacing up) {
            reset(new Orientation(up, forward));
        }

        /*public double getYaw() {
            if(buildup < 0)
                return MathHelper.clampedLerp(-lastValue.yaw, 0, buildup + 1);
            else
                return MathHelper.clampedLerp(0, nextValue.yaw, buildup);
        }

        public double getPitch() {
            if(buildup < 0)
                return MathHelper.clampedLerp(-lastValue.pitch, 0, buildup + 1);
            else
                return MathHelper.clampedLerp(0, nextValue.pitch, buildup);
        }*/

        public Quaternion getRotation() {
            Quaternion start;
            Quaternion end;
            float slide;

            if(buildup <= 0) {
                start = getRotation(lastValue);
                end = getRotation(currentValue);
                slide = (float)buildup+1;
            } else {
                start = getRotation(currentValue);
                end = getRotation(nextValue);
                slide = (float) buildup;
            }

            return Misc.slerp(start,end,slide);
        }

        private Quaternion getRotation(Orientation orientation) {
            return FacingToRotation.get(orientation.forward, orientation.up).getQuat();
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            if(lastValue == null)
                lastValue = new Orientation(EnumFacing.UP, EnumFacing.NORTH);
            if(currentValue == null)
                currentValue = new Orientation(EnumFacing.UP, EnumFacing.NORTH);
        }

        @Override
        protected NBTBase writeValue(Orientation value) {
            if(value == null)
                return null;
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("up", value.up.getName());
            compound.setString("forward", value.forward.getName());
            return compound;
        }

        @Override
        protected Orientation readValue(NBTBase nbt) {
            if(nbt == null)
                return null;
            NBTTagCompound compound = (NBTTagCompound) nbt;
            return new Orientation(
                    EnumFacing.byName(compound.getString("up")),
                    EnumFacing.byName(compound.getString("forward"))
            );
        }
    }

    protected double buildup;
    protected T lastValue;
    protected T currentValue;
    protected T nextValue;

    public TurnMachine(T currentValue) {
        reset(currentValue);
    }

    public boolean canChange() {
        return buildup == 0;
    }

    public T getCurrentValue() {
        return currentValue;
    }

    public T getStartValue() {
        if(buildup <= 0)
            return lastValue;
        else
            return currentValue;
    }

    public T getEndValue() {
        if(buildup <= 0)
            return currentValue;
        else
            return nextValue;
    }

    public T getActiveValue() {
        if(buildup < -0.25)
            return lastValue;
        else if(buildup > 0.25)
            return nextValue;
        else
            return currentValue;
    }

    public double getBuildup() {
        return buildup;
    }

    public void setNext(T nextValue) {
        this.nextValue = nextValue;
    }

    public void reset(T value) {
        lastValue = value;
        currentValue = value;
        nextValue = null;
        buildup = 0;
    }

    public void onChange() {
        //NOOP
    }

    public void update(double speed) {
        buildup += speed;

        if(nextValue != null)
            buildup = Math.min(0.5, buildup);
        else
            buildup = Math.min(0, buildup);

        if(nextValue != null && buildup >= 0.5) {
            lastValue = currentValue;
            currentValue = nextValue;
            nextValue = null;
            onChange();
            buildup = -0.5;
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTBase lastTag = writeValue(lastValue);
        NBTBase currentTag = writeValue(currentValue);
        NBTBase nextTag = writeValue(nextValue);
        if(lastTag != null)
            nbt.setTag("last", lastTag);
        if(currentTag != null)
            nbt.setTag("current", currentTag);
        if(nextTag != null)
            nbt.setTag("next", nextTag);
        nbt.setDouble("buildup", buildup);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        lastValue = readValue(nbt.getTag("last"));
        currentValue = readValue(nbt.getTag("current"));
        nextValue = readValue(nbt.getTag("next"));
        buildup = nbt.getDouble("buildup");
    }

    protected abstract NBTBase writeValue(T value);

    protected abstract T readValue(NBTBase nbt);
}
