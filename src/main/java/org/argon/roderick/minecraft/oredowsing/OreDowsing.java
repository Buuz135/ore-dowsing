package org.argon.roderick.minecraft.oredowsing;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.argon.roderick.minecraft.oredowsing.init.CommonProxy;
import org.argon.roderick.minecraft.oredowsing.init.ModItems;
import org.argon.roderick.minecraft.oredowsing.lib.Constants;
import org.argon.roderick.minecraft.oredowsing.lib.Reference;

import java.io.File;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class OreDowsing {

    @SidedProxy(clientSide = "org.argon.roderick.minecraft.oredowsing.init.ClientProxy", serverSide = "org.argon.roderick.minecraft.oredowsing.init.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), Reference.CONFIG_FILE));

        config.load();
        proxy.preInit(event);
        Constants.preInit(event, config);
        ModItems.preInit(event, config);
        config.save();
    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        proxy.init(event);
        ModItems.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
