package rustichromia.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Attributes {
    public static final IAttribute SPEARS = new RangedAttribute(null, "generic.spears", 0.0D, 0.0D, 16.0D).setShouldWatch(true);

    @SubscribeEvent
    public static void onEntityConstructEvent(EntityEvent.EntityConstructing event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(SPEARS);
        }
    }

    @SubscribeEvent
    public static void onUpdateEvent(LivingEvent.LivingUpdateEvent event)
    {
        Entity entity = event.getEntity();
        IAttributeInstance spears = ((EntityLivingBase) entity).getEntityAttribute(SPEARS);
        double value = spears.getBaseValue();
        double lastValue = value;
        value -= 0.001;
        if((int)value != (int)lastValue)
            spears.setBaseValue(0);
        else
            spears.setBaseValue(Math.max(value,0));
    }

    public static void addSpears(Entity entity, int amt) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance spears = ((EntityLivingBase) entity).getEntityAttribute(SPEARS);
            double value = spears.getBaseValue();
            double newValue = Math.floor(value + amt) + 0.9999;
            spears.setBaseValue(newValue);
        }
    }

    public static void clearSpears(Entity entity) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance spears = ((EntityLivingBase) entity).getEntityAttribute(SPEARS);
            spears.setBaseValue(0);
        }
    }
}
