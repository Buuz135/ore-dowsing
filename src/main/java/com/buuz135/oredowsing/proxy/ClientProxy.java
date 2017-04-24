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
        File oreColorFile = new File("config" + File.separator + Reference.MOD_ID + File.separator + OreDowsingConfig.file);
        if (!oreColorFile.exists()) {
            try {
                oreColorFile.createNewFile();
                FileUtils.copyFile(new File(getClass().getClassLoader().getResource("assets/oredowsing/oreColor.json").getFile()), oreColorFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            OreColor.oreColors = new Gson().fromJson(new JsonReader(new FileReader(oreColorFile)), OreColor[].class);
            checkForUncoloredEntries();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void checkForUncoloredEntries() {
        StringBuilder builder = new StringBuilder("Missing info for this oredictionary entries: ");
        for (String s : OreDictionary.getOreNames()) {
            //System.out.println(s); //Todo Check
        }
    }
}
