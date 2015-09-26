package org.argon.roderick.minecraft.oredowsing.init;

import java.util.ArrayList;

import org.argon.roderick.minecraft.oredowsing.items.DowsingRod;
import org.argon.roderick.minecraft.oredowsing.lib.Constants;
import org.argon.roderick.minecraft.oredowsing.lib.Reference;
import org.argon.roderick.minecraft.oredowsing.recipe.RecipeDowsingRodUpgrade;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ModItems
{
    public static ArrayList<DowsingRod> dowsingRods = new ArrayList<DowsingRod>();

    public static DowsingRod    woodDowsingRod;
    public static DowsingRod    ironDowsingRod;
    public static DowsingRod    goldDowsingRod;
    public static DowsingRod diamondDowsingRod;

    public static DowsingRod createDowsingRod(String parNamePrefix,
            Object parIngredientBase, Object parIngredientTop,
            Block parForcedTargetBlock,
            int parMaxDamage, int parSquareRadius, boolean parIsChargeable,
            int parDiamondsPerUpgrade, int parMaxUpgrades)
    {
        DowsingRod rod = new DowsingRod(parNamePrefix,
                parIngredientBase, parIngredientTop,
                parForcedTargetBlock,
                parMaxDamage, parSquareRadius, parIsChargeable,
                parDiamondsPerUpgrade, parMaxUpgrades);
        dowsingRods.add(rod);
        return rod;
    }

    public static void preInit(FMLPreInitializationEvent event)
    {
           woodDowsingRod = createDowsingRod("Wood",    Items.stick,      Items.coal,                                        Blocks.iron_ore,      50, 4, false, 0, 0);
           ironDowsingRod = createDowsingRod("Iron",    Items.iron_ingot, Items.redstone,                                    Blocks.gold_ore,     100, 6, false, 0, 0);
           goldDowsingRod = createDowsingRod("Gold",    Items.gold_ingot, new ItemStack(Items.dye, 1, Constants.META_LAPIS), Blocks.diamond_ore,  100, 8, false, 0, 0);
        diamondDowsingRod = createDowsingRod("Diamond", Items.diamond,    Items.emerald,                                     null,               1000, 8, true,  4, 8);

        for (DowsingRod rod : dowsingRods) {
            GameRegistry.registerItem(rod, rod.getName());
        }
    }

    public static void init(FMLInitializationEvent event)
    {
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

}
