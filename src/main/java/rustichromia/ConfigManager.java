package rustichromia;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigManager {
    public static Configuration config;

    public static int quernOreAmount;
    public static int quernFlowerAmount;
    public static int windmillBlades;
    public static int windmillBigBlades;

    public static void init(File configFile)
    {
        MinecraftForge.EVENT_BUS.register(ConfigManager.class);

        if(config == null)
        {
            config = new Configuration(configFile);
            load();
        }
    }

    public static void load() {
        quernOreAmount = config.get("quernOreAmount","machine", 2, "Multiplier for how much should be produced when processing ores. 0 to disable.", 0, Integer.MAX_VALUE).getInt();
        quernFlowerAmount = config.get("quernFlowerAmount","machine", 4, "Multiplier for how much should be produced when processing flowers. 0 to disable.", 0, Integer.MAX_VALUE).getInt();
        windmillBlades = config.get("windmillBlades","machine", 12, "How many blades can be attached to the small windmill.").getInt();
        windmillBigBlades = config.get("windmillBigBlades","machine", 8, "How many blades can be attached to the big windmill.").getInt();

        if (config.hasChanged())
        {
            config.save();
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equalsIgnoreCase(Rustichromia.MODID))
        {
            load();
        }
    }
}
