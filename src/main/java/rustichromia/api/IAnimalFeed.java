package rustichromia.api;

import net.minecraft.entity.Entity;

public interface IAnimalFeed {
    boolean canFeed(Entity entity);

    void feed(Entity entity);
}
