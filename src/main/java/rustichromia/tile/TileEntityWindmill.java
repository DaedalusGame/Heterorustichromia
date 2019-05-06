package rustichromia.tile;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import rustichromia.block.BlockWindmill;

public class TileEntityWindmill extends TileEntity implements ITickable {
    double lastAngle;
    double angle;
    private double currentPower = 1;

    public DefaultMechCapability capability = new DefaultMechCapability() {
        @Override
        public double getPower(EnumFacing from) {
            return currentPower;
        }

        @Override
        public void setPower(double value, EnumFacing from) {
            if (from == null) {
                currentPower = value;
                onPowerChange();
            }
        }

        @Override
        public boolean isOutput(EnumFacing face) {
            return true;
        }

        @Override
        public boolean isInput(EnumFacing face) {
            return false;
        }

        @Override
        public void onPowerChange() {
            updateNeighbors();
            markDirty();
        }
    };

    public double getScale() {
        IBlockState state = world.getBlockState(pos);
        BlockWindmill block = (BlockWindmill) state.getBlock();
        return block.getScale(world,pos,state);
    }

    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        return state.getValue(BlockWindmill.facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing()) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == MysticalMechanicsAPI.MECH_CAPABILITY && facing == getFacing()) {
            return MysticalMechanicsAPI.MECH_CAPABILITY.cast(this.capability);
        }
        return super.getCapability(capability, facing);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        capability.setPower(0f, null);
        updateNeighbors();
    }

    public void updateNeighbors() {
        EnumFacing facing = getFacing();
        TileEntity tile = world.getTileEntity(getPos().offset(facing));
        if (tile != null) {
            if (tile.hasCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite())) {
                if (tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).isInput(facing.getOpposite())) {
                    tile.getCapability(MysticalMechanicsAPI.MECH_CAPABILITY, facing.getOpposite()).setPower(capability.getPower(facing.getOpposite()), facing.getOpposite());
                }
            }
        }
    }

    @Override
    public void update() {
        if (world.isRemote) {
            lastAngle = angle;
            angle += capability.getPower(null);
        }
    }
}
