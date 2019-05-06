package rustichromia.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rustichromia.util.DimensionPos;

import javax.annotation.Nullable;
import java.util.*;

public class PistonHandler {
    private static Set<TileEntityPiston> activePistons =  Collections.synchronizedSet(new HashSet<>());
    private static Map<DimensionPos,EnumFacing> pushes = new HashMap<>();

    @SubscribeEvent
    public static void pistonInit(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity te = event.getObject();
        if (te instanceof TileEntityPiston) {
            activePistons.add((TileEntityPiston) te);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void pistonTick(TickEvent.ServerTickEvent event) {
        if(event.phase != TickEvent.Phase.END)
            return;
        TileEntityPiston[] toIterate = activePistons.toArray(new TileEntityPiston[activePistons.size()]);
        HashSet<TileEntityPiston> toRemove = new HashSet<>();
        pushes.clear();
        for (TileEntityPiston piston: toIterate) {
            World world = piston.getWorld();
            toRemove.add(piston);

            if(world != null && !world.isRemote && piston != null)
            {
                BlockPos pos = piston.getPos();
                EnumFacing facing = piston.getFacing();
                pushes.put(new DimensionPos(pos,world.provider.getDimension()),facing);
            }
        }

        activePistons.removeAll(toRemove);
    }

    @Nullable
    public static EnumFacing getPushDirection(DimensionPos pos) {
        return pushes.getOrDefault(pos,null);
    }
}
