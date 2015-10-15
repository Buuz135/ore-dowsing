package org.argon.roderick.minecraft.oredowsing.init;

import java.util.ArrayList;

import org.argon.roderick.minecraft.oredowsing.items.DowsingRod;
import org.argon.roderick.minecraft.oredowsing.lib.Constants;
import org.argon.roderick.minecraft.oredowsing.lib.RegisterHelper;
import org.argon.roderick.minecraft.oredowsing.recipe.RecipeDowsingRodUpgrade;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class ModItems
{
    public static ArrayList<DowsingRod> dowsingRods = new ArrayList<DowsingRod>();

    public static void createDowsingRod(Configuration config,
            String parNamePrefix,
            String parIngredientBaseName,
            String parIngredientTopName,
            String parForcedTargetBlockName,
            int parMaxDamage,
            int parSquareRadius,
            // XXX use colors boolean
            boolean parIsChargeable,
            // XXX allow change boolean
            // XXX upgrade item name
            int parDiamondsPerUpgrade,
            int parMaxSquareRadius)
    {
        String  namePrefix         = parNamePrefix;
        String  cat                = namePrefix.toLowerCase() + "_dowsing_rod";

        ItemStack ingredientBase   = getStackForString(
            config.get(cat, "ingredient_base", parIngredientBaseName, "main crafting ingredient").getString());
        ItemStack ingredientTop    = getStackForString(
            config.get(cat, "ingredient_tip", parIngredientTopName, "crafting ingredient for tip").getString());
        Block   forcedTargetBlock  = getBlockForString(
            config.get(cat, "target_block", parForcedTargetBlockName, "block detected, empty for all ores").getString());
        int     maxDamage          = config.get(cat, "max_damage",           parMaxDamage         , "number of uses").getInt();
        int     squareRadius       = config.get(cat, "square_radius",        parSquareRadius      , "detection area is 1+2*radius").getInt();
        boolean isChargeable       = config.get(cat, "is_chargeable",        parIsChargeable      , "true to allow repairing with RF").getBoolean();
        int     diamondsPerUpgrade = config.get(cat, "diamonds_per_upgrade", parDiamondsPerUpgrade, "> 0 allows upgrading radius").getInt();
        int     maxSquareRadius    = config.get(cat, "max_square_radius",    parMaxSquareRadius   , "maximum upgraded radius").getInt();

        if (!config.get(cat, "enabled", true, "false to disable this dowsing rod completely").getBoolean()) {
            return;
        }

        DowsingRod rod = new DowsingRod(namePrefix,
                ingredientBase, ingredientTop,
                forcedTargetBlock,
                maxDamage, squareRadius, isChargeable,
                diamondsPerUpgrade, maxSquareRadius);
        dowsingRods.add(rod);
    }

    public static void preInit(FMLPreInitializationEvent event, Configuration config)
    {
        // Possible configuration improvements:
        // - set up category hierarchy to hold the per-rod config settings
        //   rather than different top-level categories
        // - read list of rods to create from config (getStringList())

        createDowsingRod(config, "Wood",    "minecraft:stick",      "minecraft:coal",                        "minecraft:iron_ore",      50, 4, false, 0,  0);
        createDowsingRod(config, "Iron",    "minecraft:iron_ingot", "minecraft:redstone",                    "minecraft:gold_ore",     100, 6, false, 0,  0);
        createDowsingRod(config, "Gold",    "minecraft:gold_ingot", "minecraft:dye;" + Constants.META_LAPIS, "minecraft:diamond_ore",  100, 8, false, 0,  0);
        createDowsingRod(config, "Diamond", "minecraft:diamond",    "minecraft:emerald",                     "",                      1000, 8, true,  4, 16);

        for (DowsingRod rod : dowsingRods) {
            RegisterHelper.registerItem(rod);
        }
    }

    public static void init(FMLInitializationEvent event)
    {
        for (DowsingRod rod : dowsingRods) {
            GameRegistry.addRecipe(
                   new ItemStack(rod),
                   " y ",
                   "xxx",
                   "x x",
                   'x', rod.ingredientBase,
                   'y', rod.ingredientTop);
        }

        GameRegistry.addRecipe(new RecipeDowsingRodUpgrade());
    }

    // cribbed from EnderIO

    public static ItemStack getStackForString(String s) {
        String[] nameAndMeta = s.split(";");
        int meta = nameAndMeta.length == 1 ? 0 : Integer.parseInt(nameAndMeta[1]);
        String[] data = nameAndMeta[0].split(":");
        if (data.length < 2) {
            return null;
        }
        ItemStack stack = GameRegistry.findItemStack(data[0], data[1], 1);
        if(stack == null) {
            return null;
        }
        stack.setItemDamage(meta);
        return stack;
    }

    public static Block getBlockForString(String s) {
        String[] data = s.split(":");
        return data.length < 2 ? null : GameRegistry.findBlock(data[0], data[1]);
    }

}
