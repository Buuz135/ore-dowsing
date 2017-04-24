package com.buuz135.oredowsing.item;

import com.buuz135.oredowsing.util.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item {

    public ItemBase(String name, int stackSize) {
        setUnlocalizedName(Reference.MOD_ID + ":" + name);
        setMaxStackSize(stackSize);
        setCreativeTab(CreativeTabs.TOOLS);
    }
}
