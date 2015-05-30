package org.argon.roderick.minecraft.oredowsing;

import org.argon.roderick.minecraft.oredowsing.init.CommonProxy;
import org.argon.roderick.minecraft.oredowsing.init.ModItems;
import org.argon.roderick.minecraft.oredowsing.lib.Reference;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class OreDowsing {

    @SidedProxy(clientSide="org.argon.roderick.minecraft.oredowsing.init.ClientProxy", serverSide="org.argon.roderick.minecraft.oredowsing.init.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
        ModItems.preInit();
    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event)
    {
        proxy.init(event);
        ModItems.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

}
