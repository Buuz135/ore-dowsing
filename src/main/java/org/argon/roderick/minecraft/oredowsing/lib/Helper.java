package org.argon.roderick.minecraft.oredowsing.lib;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.argon.roderick.minecraft.oredowsing.init.ModItems;

public class Helper {

    public static boolean isDowsingRod(Item item)
    {
        // XXX is there a better way to do this?
        return item == ModItems.woodDowsingRod
                || item == ModItems.ironDowsingRod
                || item == ModItems.goldDowsingRod
                || item == ModItems.diamondDowsingRod;
    }

    public static boolean isDowsingRod(ItemStack stack)
    {
        return isDowsingRod(stack.getItem());
    }

}
