package rustichromia.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import rustichromia.block.BlockThatchBed;

public class RestHandler {
    @SubscribeEvent
    public static void onTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side == Side.SERVER) {
            World world = event.world;
            for(EntityPlayer player : world.playerEntities) {
                BlockPos bedLocation = player.bedLocation;

                if(bedLocation != null && world.getBlockState(bedLocation).getBlock() instanceof BlockThatchBed) {
                    player.sleepTimer = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public static void allowDaytimeNapping(SleepingTimeCheckEvent evt) {
        EntityPlayer player = evt.getEntityPlayer();
        World world = player.getEntityWorld();
        BlockPos pos = evt.getSleepingLocation();

        if (pos != null) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockThatchBed) {
                evt.setResult(Event.Result.ALLOW);
            }
        }
    }

    @SubscribeEvent
    public static void onSpawnPointChange(PlayerSetSpawnEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        BlockPos pos = event.getNewSpawn();

        if(!world.isRemote && pos != null && world.getBlockState(pos).getBlock() instanceof BlockThatchBed) {
            event.setCanceled(true);
        }
    }
}
