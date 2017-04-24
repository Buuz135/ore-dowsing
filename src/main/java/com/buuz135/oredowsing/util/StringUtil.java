package com.buuz135.oredowsing.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;

public class StringUtil {

    public static String getStringWithColor(String string, TextFormatting formatting) {
        return new TextComponentString(string).setStyle(new Style().setColor(formatting)).getFormattedText();
    }


    public static boolean isOre(Block block, IBlockState state) {
        ItemStack stack = new ItemStack(block, 1, block.damageDropped(state));
        if (stack.isEmpty()) {
            return false;
        }
        int ore_ids[] = OreDictionary.getOreIDs(stack);
        for (int i = 0; i < ore_ids.length; i++) {
            if (OreDictionary.getOreName(ore_ids[i]).startsWith("ore")) {
                return true;
            }
        }
        return false;
    }
}
