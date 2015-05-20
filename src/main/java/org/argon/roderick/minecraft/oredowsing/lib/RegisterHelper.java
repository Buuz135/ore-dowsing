package org.argon.roderick.minecraft.oredowsing.lib;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class RegisterHelper
{

    public static void registerItem(Item item)
    {
        GameRegistry.registerItem(item, Reference.MODID + item.getUnlocalizedName().substring(5));
    }

}
