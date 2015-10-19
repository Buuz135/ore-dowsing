package org.argon.roderick.minecraft.oredowsing.lib;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class Helper {

    // cribbed from EnderIO
    public static ItemStack getStackForString(String s) {
        String[] nameAndMeta = s.split(";");
        int meta = nameAndMeta.length == 1 ? 0 : Integer.parseInt(nameAndMeta[1]);
        String[] data = nameAndMeta[0].split(":");
        if (data.length != 2) {
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
        return data.length != 2 ? null : GameRegistry.findBlock(data[0], data[1]);
    }

}
