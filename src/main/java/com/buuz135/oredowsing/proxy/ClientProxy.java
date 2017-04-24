package com.buuz135.oredowsing.proxy;

import com.buuz135.oredowsing.OreDowsing;
import com.buuz135.oredowsing.config.OreColor;
import com.buuz135.oredowsing.config.OreDowsingConfig;
import com.buuz135.oredowsing.event.RenderWorldEvent;
import com.buuz135.oredowsing.util.Reference;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
    }

    @Override
    public void init() {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(OreDowsing.rod, 0, new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "dowsing_rod"), "inventory"));
        MinecraftForge.EVENT_BUS.register(new RenderWorldEvent());
    }

    @Override
    public void postInit() {

    }


}
