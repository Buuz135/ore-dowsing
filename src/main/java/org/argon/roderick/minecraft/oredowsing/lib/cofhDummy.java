// replacements for items from CofHLib I was using (because it isn't available for Minecraft 1.8)

package org.argon.roderick.minecraft.oredowsing.lib;


import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;

public class cofhDummy {
	public static final String ORE = "ore";

    public static boolean isOre(ItemStack stack) {
    	if (stack == null || stack.isEmpty()) {
    		return false;
    	}
    	
    	int ore_ids[] = OreDictionary.getOreIDs(stack);
    	for (int i = 0; i < ore_ids.length; i++) {
    		if (OreDictionary.getOreName(ore_ids[i]).startsWith(ORE)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }
    
    public static String localize(String key) { //TODO Fix
        return key;
    }

}
