package rustichromia.tile;

import mysticalmechanics.api.DefaultMechCapability;
import mysticalmechanics.api.MysticalMechanicsAPI;
import mysticalmechanics.util.Misc;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import rustichromia.block.BlockMechTorch;

import javax.annotation.Nullable;

public class TileEntityMechTorchToggle extends TileEntityMechTorch {
    @Override
    protected void handleRedstone() {
        IBlockState state = world.getBlockState(pos);
        if(on && pulses > 0) {
            world.setBlockState(pos,state.withProperty(BlockMechTorch.on,false));
            on = false;
            pulses--;
        } else if(pulses > 0) {
            world.setBlockState(pos,state.withProperty(BlockMechTorch.on,true));
            on = true;
            pulses--;
        }
    }
}
