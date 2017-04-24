package com.buuz135.oredowsing.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.argon.roderick.minecraft.oredowsing.lib.Reference;

public class ItemBase extends Item {

    public ItemBase(String name, int stackSize) {
        setUnlocalizedName(Reference.MODID + ":" + name);
        setMaxStackSize(stackSize);
        setCreativeTab(CreativeTabs.TOOLS);
    }
}
