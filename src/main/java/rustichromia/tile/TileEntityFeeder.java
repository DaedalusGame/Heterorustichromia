package rustichromia.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import rustichromia.api.IAnimalFeed;

import java.util.List;
import java.util.Random;

public class TileEntityFeeder extends TileEntity implements ITickable {
    Random random = new Random();
    int workTime;

    @Override
    public void update() {
        IBlockState topState = world.getBlockState(getPos().up());
        Block topBlock = topState.getBlock();

        if(topBlock instanceof IAnimalFeed)
        {
            workTime++;
            if(workTime > 20) {
                IAnimalFeed feed = (IAnimalFeed) topBlock;
                List<Entity> animals = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPos().up()).grow(5, 0, 5), feed::canFeed);
                if (!animals.isEmpty()) {
                    Entity animal = animals.get(random.nextInt(animals.size()));
                    feed.feed(animal);
                    world.setBlockToAir(getPos().up());
                }
                workTime = 0;
            }
        }
    }
}
