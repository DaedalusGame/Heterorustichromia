package rustichromia.tile;

import mysticalmechanics.api.IMechCapability;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityAxleWood extends TileEntityAxleBase {
    int checkTime;

    @Override
    public void update() {
        super.update();
        if(!world.isRemote) {
            checkTime++;
            if(checkTime > 300) {
                double speed = ((IMechCapability)capability).getPower(null);
                if(speed > 20 && world.rand.nextDouble() < 0.01 * (speed - 20))
                    breakAxle(false);
                else if(speed > 0 && getLength() > 5)
                    breakAxle(true);

                checkTime = 0;
            }
        }
    }

    private void breakAxle(boolean dropItem) {
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
}
