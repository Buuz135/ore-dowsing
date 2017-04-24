package com.buuz135.oredowsing.config;

import com.buuz135.oredowsing.util.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID + "/" + Reference.MOD_ID)
public class OreDowsingConfig {


    @Config.Comment("Ore rendering color config file")
    public static String file = "oreColor.json";

    @Config.Comment("Starting radius size of the rods")
    public static int minSize = 7;

    @Config.Comment("Radius increased per efficiency level")
    public static int efficiencyIncrease = 2;

    public static Rendering rendering = new Rendering();

    public static class Rendering {
        @Config.Comment("Seconds the render will be displayed.")
        @Config.RangeDouble(min = 0, max = 60)
        public static int renderTime = 30;

    }

    @Mod.EventBusSubscriber
    public static class ConfigurationHolder {

        private static final MethodHandle CONFIGS_GETTER = findFieldGetter(ConfigManager.class, "CONFIGS");

        private static Configuration configuration;

        public static Configuration getConfiguration() {
            if (configuration == null) {
                try {
                    final String fileName = Reference.MOD_ID + ".cfg";

                    final Map<String, Configuration> configsMap = (Map<String, Configuration>) CONFIGS_GETTER.invokeExact();

                    for (Map.Entry<String, Configuration> stringConfigurationEntry : configsMap.entrySet()) {
                        if (fileName.equals(new File(stringConfigurationEntry.getKey()).getName())){
                            configuration = stringConfigurationEntry.getValue();
                            break;
                        }
                    }
                } catch (Throwable throwable) {
                    //Logger.error(throwable, "Failed to get Configuration instance");
                }
            }

            return configuration;
        }

        public static MethodHandle findFieldGetter(Class<?> clazz, String... fieldNames) {
            final Field field = ReflectionHelper.findField(clazz, fieldNames);

            try {
                return MethodHandles.lookup().unreflectGetter(field);
            } catch (IllegalAccessException e) {
                throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, e);
            }
        }

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Reference.MOD_ID)) {
                ConfigManager.load(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
