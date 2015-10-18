package org.argon.roderick.minecraft.oredowsing.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Constants {

    // Minecraft
    public static final int META_LAPIS = 4;
    public static final int TICKS_PER_SEC = 20;

    // mine, final
    public static final int DAMAGE_PER_USE = 1;

    // mine, configurable
    public static double RENDER_DURATION;
    public static int    RF_PER_DAMAGE;

    public static void preInit(FMLPreInitializationEvent event, Configuration config)
    {
        RENDER_DURATION = config.get(Configuration.CATEGORY_GENERAL, "render_duration", 30.0D, "duration that the block outline stays on the screen (seconds)").getDouble();
        RF_PER_DAMAGE   = config.get(Configuration.CATEGORY_GENERAL, "rf_per_use",       3000, "RF to recharge/repair 1 use").getInt();
    }
}
