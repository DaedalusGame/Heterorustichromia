package rustichromia;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.HashMap;

public class ConfigManager {
    public static Configuration config;

    public static int quernOreAmount;
    public static int quernFlowerAmount;

    public static int windmillBlades;
    public static double windmillWeight;
    public static double windmillPowerMod;
    public static double windmillBladePower;
    public static double windmillBladePenalty;
    public static int windmillMinHeight;

    public static int windmillBigBlades;
    public static double windmillBigWeight;
    public static double windmillBigPowerMod;
    public static double windmillBigBladePower;
    public static double windmillBigBladePenalty;
    public static int windmillBigMinHeight;

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
        quernOreAmount = config.get("machine", "quernOreAmount",2, "Multiplier for how much should be produced when processing ores. 0 to disable.", 0, Integer.MAX_VALUE).getInt();
        quernFlowerAmount = config.get("machine","quernFlowerAmount", 4, "Multiplier for how much should be produced when processing flowers. 0 to disable.", 0, Integer.MAX_VALUE).getInt();

        windmillBlades = config.get("machine","windmillBlades", 12, "How many blades can be attached to the small windmill.").getInt();
        windmillWeight = config.get("machine","windmillWeight", 1.0, "{Deprecated} How heavy is each blade of the small windmill.").getDouble();
        windmillPowerMod = config.get("machine","windmillPowerMod", 7.0, "How much (external) power does a small windmill produce nominally.").getDouble();
        windmillBladePower = config.get("machine","windmillBladePower", 5.0, "How much (internal) power 1 blade of the small windmill generates.").getDouble();
        windmillBladePenalty = config.get("machine","windmillBladePenalty", 0.2, "How much (internal) power is lost for each blade of the small windmill over the default count.").getDouble();
        windmillMinHeight = config.get("machine","windmillMinHeight", 0, "How high above sea level must a small windmill be to produce any power.").getInt();

        windmillBigBlades = config.get("machine","windmillBigBlades", 8, "How many blades can be attached to the big windmill.").getInt();
        windmillBigWeight = config.get("machine","windmillBigWeight", 8.0, "{Deprecated} How heavy is each blade of the big windmill.").getDouble();
        windmillBigPowerMod = config.get("machine","windmillBigPowerMod", 7.0, "How much (external) power does a big windmill produce nominally.").getDouble();
        windmillBigBladePower = config.get("machine","windmillBigBladePower", 25.0, "How much (internal) power 1 blade of the big windmill generates.").getDouble();
        windmillBigBladePenalty = config.get("machine","windmillBigBladePenalty", 2.0, "How much (internal) power is lost for each blade of the big windmill over the default count.").getDouble();
        windmillBigMinHeight = config.get("machine","windmillBigMinHeight", 20, "How high above sea level must a big windmill be to produce any power.").getInt();

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
