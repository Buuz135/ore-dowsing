package org.argon.roderick.minecraft.oredowsing.init;

import org.argon.roderick.minecraft.oredowsing.items.DowsingRod;
import org.argon.roderick.minecraft.oredowsing.lib.RegisterHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModItems extends Item
{

	public static Item    woodDowsingRod = new DowsingRod("Wood",    Blocks.iron_ore,      50, 4);
	public static Item    ironDowsingRod = new DowsingRod("Iron",    Blocks.gold_ore,     100, 6);
	public static Item    goldDowsingRod = new DowsingRod("Gold",    Blocks.diamond_ore,  100, 8);
	public static Item diamondDowsingRod = new DowsingRod("Diamond", null,               1000, 8);
    
    public static void init()
    {
    	RegisterHelper.registerItem(woodDowsingRod);
    	GameRegistry.addRecipe(
    			new ItemStack(woodDowsingRod),
    			" y ",
    		    "xxx",
    		    "x x",
    		    'x', new ItemStack(Items.stick),
    		    'y', new ItemStack(Items.coal));

    	RegisterHelper.registerItem(ironDowsingRod);
    	GameRegistry.addRecipe(
    			new ItemStack(ironDowsingRod),
    			" y ",
    		    "xxx",
    		    "x x",
    		    'x', new ItemStack(Items.iron_ingot),
    		    'y', new ItemStack(Items.redstone));

    	RegisterHelper.registerItem(goldDowsingRod);
    	GameRegistry.addRecipe(
    			new ItemStack(goldDowsingRod),
    			" y ",
    		    "xxx",
    		    "x x",
    		    'x', new ItemStack(Items.gold_ingot),
    		    'y', new ItemStack(Items.dye));

    	RegisterHelper.registerItem(diamondDowsingRod);
    	GameRegistry.addRecipe(
    			new ItemStack(diamondDowsingRod),
    			" y ",
    		    "xxx",
    		    "x x",
    		    'x', new ItemStack(Items.diamond),
    		    'y', new ItemStack(Items.emerald));

    }

}