package org.argon.roderick.minecraft.oredowsing.init;

import org.argon.roderick.minecraft.oredowsing.render.DowsingRodRenderer;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    public void init(FMLInitializationEvent e)
    {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new DowsingRodRenderer());
    }

}
