package rustichromia.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import rustichromia.cart.CartDataClient;

public class EntityCart extends Entity {
    CartDataClient data;

    public EntityCart(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 0.6F);
    }

    public CartDataClient getData() {
        return data;
    }

    public void setData(CartDataClient data) {
        this.data = data;
    }

    @Override
    protected void entityInit() {
        //NOOP
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if(data.isDestroyed())
            setDead();
    }

    @Override
    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        //NOOP
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        //NOOP
    }
}
