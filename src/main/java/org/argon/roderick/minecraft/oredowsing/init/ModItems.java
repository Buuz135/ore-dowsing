package org.argon.roderick.minecraft.oredowsing.init;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.argon.roderick.minecraft.oredowsing.items.DowsingRod;
import org.argon.roderick.minecraft.oredowsing.lib.Constants;
import org.argon.roderick.minecraft.oredowsing.lib.Reference;
import org.argon.roderick.minecraft.oredowsing.recipe.RecipeDowsingRodUpgrade;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static ArrayList<DowsingRod> dowsingRods = new ArrayList<DowsingRod>();

    // configuration helper which puts properties in the order in which they
    // were called

    private static class OrderedConfigCategory {
        Configuration config;
        String cat;
        List<String> propOrder = new ArrayList<String>();

        public OrderedConfigCategory(Configuration parConfig, String parCategory) {
            this.config = parConfig;
            this.cat = parCategory;
        }

        public void close() {
            config.setCategoryPropertyOrder(this.cat, this.propOrder);
        }

        public boolean get(String key, boolean def, String comment) {
            this.propOrder.add(key);
            return config.get(cat, key, def, comment).getBoolean();
        }

        public int get(String key, int def, String comment) {
            this.propOrder.add(key);
            return config.get(cat, key, def, comment).getInt();
        }

        public String get(String key, String def, String comment) {
            this.propOrder.add(key);
            return config.get(cat, key, def, comment).getString();
        }

    }

    public static void createDowsingRod(
            Configuration parConfig,
            String parNamePrefix,
            String parIngredientBaseName,
            String parIngredientTopName,
            String parForcedTargetBlockName,
            boolean parAllowTargetChange,
            int parMaxDamage,
            int parSquareRadius,
            boolean parShowOreColor,
            boolean parIsChargeable,
            String parUpgradeItemName,
            int parItemsPerUpgrade,
            int parMaxSquareRadius) {
        String namePrefix = parNamePrefix;
        String cat = "rod." + namePrefix.toLowerCase();
        OrderedConfigCategory myconf = new OrderedConfigCategory(parConfig, cat);

        //System.out.println("configuring " + cat);
        parConfig.setCategoryComment(cat, "settings for the " + namePrefix.toLowerCase() + " dowsing rod");

        boolean enabled = myconf.get("enabled", true, "false to disable this dowsing rod completely");
        ItemStack ingredientBase =
                getStackForString(myconf.get("ingredient_base", parIngredientBaseName, "main crafting ingredient"));
        ItemStack ingredientTop =
                getStackForString(myconf.get("ingredient_tip", parIngredientTopName, "crafting ingredient for tip"));
        Block forcedTargetBlock =
                getBlockForString(myconf.get("target_block", parForcedTargetBlockName, "block detected, empty for all ores"));
        boolean allowTargetChange = myconf.get("allow_target_block_change", parAllowTargetChange, "true to allow changing which block is detected");
        int maxDamage = myconf.get("num_uses", parMaxDamage, "number of uses");
        int squareRadius = myconf.get("radius_base", parSquareRadius, "detection area is 1+2*radius cube");
        boolean showOreColor = myconf.get("show_ore_color", parShowOreColor, "true to color detected ores by type");
        boolean isChargeable = false; /*myconf.get("is_chargeable", parIsChargeable, "true to allow repairing with RF");*/
        ItemStack upgradeItem =
                getStackForString(myconf.get("upgrade_item", parUpgradeItemName, "crafting ingredient used to upgrade radius"));
        int itemsPerUpgrade = myconf.get("upgrade_item_count", parItemsPerUpgrade, "number of upgrade items required to increase radius by 1");
        int maxSquareRadius = myconf.get("radius_max", parMaxSquareRadius, "maximum upgraded radius, 0 if not upgradeable");

        myconf.close();

        if (!enabled) {
            return;
        }

        DowsingRod rod = new DowsingRod(namePrefix,
                ingredientBase, ingredientTop,
                forcedTargetBlock, allowTargetChange,
                maxDamage, squareRadius, showOreColor, isChargeable,
                upgradeItem, itemsPerUpgrade, maxSquareRadius);
        dowsingRods.add(rod);
    }

    public static void preInit(FMLPreInitializationEvent event, Configuration config) {
        //                        name       base ingredient         tip ingredient                           target                 change |uses|radius|color|charge|        upgrade item|upgrade cost|max radius
        createDowsingRod(config, "Wood", "minecraft:stick", "minecraft:coal", "minecraft:iron_ore", false, 50, 4, true, false, "", 0, 0);
        createDowsingRod(config, "Iron", "minecraft:iron_ingot", "minecraft:redstone", "minecraft:gold_ore", false, 100, 6, true, false, "", 0, 0);
        createDowsingRod(config, "Gold", "minecraft:gold_ingot", "minecraft:dye;" + Constants.META_LAPIS, "minecraft:diamond_ore", false, 100, 8, true, false, "", 0, 0);
        createDowsingRod(config, "Diamond", "minecraft:diamond", "minecraft:emerald", "", true, 1000, 8, true, true, "minecraft:diamond", 4, 16);

        for (DowsingRod rod : dowsingRods) {
            GameRegistry.register(rod, new ResourceLocation(Reference.MODID + ":" + rod.getName()));
        }
    }

    public static void init(FMLInitializationEvent event) {
        GameRegistry.addRecipe(new RecipeDowsingRodUpgrade());
        for (DowsingRod rod : dowsingRods) {
            GameRegistry.addRecipe(
                    new ItemStack(rod),
                    " y ",
                    "xxx",
                    "x x",
                    'x', rod.ingredientBase,
                    'y', rod.ingredientTop);
            if (event.getSide() == Side.CLIENT) {
                RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

                renderItem.getItemModelMesher().register(rod, 0,
                        new ModelResourceLocation(Reference.MODID + ":" + rod.getName(), "inventory"));
            }
        }
    }

    // cribbed from EnderIO

    public static ItemStack getStackForString(String s) {
        String[] nameAndMeta = s.split(";");
        int meta = nameAndMeta.length == 1 ? 0 : Integer.parseInt(nameAndMeta[1]);
        String[] data = nameAndMeta[0].split(":");
        if (data.length < 2) {
            return null;
        }
        Item item = Item.getByNameOrId(s);
        if (item == null) {
            return null;
        }
        return new ItemStack(item, 1, meta);
    }

    public static Block getBlockForString(String s) {
        String[] data = s.split(":");
        return data.length < 2 ? null : Block.getBlockFromName(s);
    }

}
