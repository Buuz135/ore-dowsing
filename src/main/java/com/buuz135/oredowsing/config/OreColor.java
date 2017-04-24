package com.buuz135.oredowsing.config;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;

public @Data
class OreColor {

    public static OreColor[] oreColors;

    public static OreColor getOreColorFromBlock(Block block) {
        for (OreColor oreColor : oreColors) {
            int ore_ids[] = OreDictionary.getOreIDs(new ItemStack(block));
            for (int i = 0; i < ore_ids.length; i++) {
                String ore_name = OreDictionary.getOreName(ore_ids[i]);
                if (Arrays.asList(oreColor.getOreDictionayEntries()).contains(ore_name)) {
                    return oreColor;
                }
            }
        }
        return null;
    }

    private String[] oreDictionayEntries;
    @SerializedName("color")
    private Color color;
    private int value;
    private boolean nether;

    public @Data
    class Color {
        private int red;
        private int green;
        private int blue;
    }
}
