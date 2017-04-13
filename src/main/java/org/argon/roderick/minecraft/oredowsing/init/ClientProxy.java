package org.argon.roderick.minecraft.oredowsing.init;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.argon.roderick.minecraft.oredowsing.render.DowsingRodRenderer;

public class ClientProxy extends CommonProxy {

    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new DowsingRodRenderer());
    }

}
