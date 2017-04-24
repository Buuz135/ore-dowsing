package com.buuz135.oredowsing.gui;

import com.buuz135.oredowsing.config.OreDowsingConfig;
import com.buuz135.oredowsing.util.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class GuiOreDowsingConfig extends GuiConfig {

    public GuiOreDowsingConfig(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), Reference.MOD_ID, false, false, I18n.format("test"));
    }

    private static List<IConfigElement> getConfigElements() {
        final Configuration configuration = OreDowsingConfig.ConfigurationHolder.getConfiguration();

        final ConfigCategory topLevelCategory = configuration.getCategory(Configuration.CATEGORY_GENERAL);
        topLevelCategory.getChildren().forEach(configCategory -> configCategory.setLanguageKey(Reference.MOD_ID + configCategory.getName()));

        return new ConfigElement(topLevelCategory).getChildElements();
    }

}
