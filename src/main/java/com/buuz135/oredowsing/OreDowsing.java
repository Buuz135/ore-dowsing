package com.buuz135.oredowsing;

import com.buuz135.oredowsing.config.OreColor;
import com.buuz135.oredowsing.config.OreDowsingConfig;
import com.buuz135.oredowsing.item.DowsingRod;
import com.buuz135.oredowsing.proxy.CommonProxy;
import com.buuz135.oredowsing.util.Reference;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class OreDowsing {

    @SidedProxy(clientSide = Reference.PROXY_CLIENT, serverSide = Reference.PROXY_COMMON)
    private static CommonProxy proxy;

    public static DowsingRod rod;
    public static DowsingRod creative;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.register(rod = new DowsingRod("dowsing_rod", 1, 1000, 500, false), new ResourceLocation(Reference.MOD_ID, "dowsing_rod"));
       // GameRegistry.register(creative = new DowsingRod("dowsing_rod_creative", 1, 1000, 500, true), new ResourceLocation(Reference.MOD_ID, "dowsing_rod_creative"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rod)," O ","LLL","L L", 'O',"oreIron",'L',"logWood"));
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
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
