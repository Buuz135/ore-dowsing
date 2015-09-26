package org.argon.roderick.minecraft.oredowsing.recipe;

import org.argon.roderick.minecraft.oredowsing.items.DowsingRod;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeDowsingRodUpgrade implements IRecipe {

    private class ParsedRecipe {
        ItemStack dowsingRodStack;
        DowsingRod dowsingRodItem;
        int numDiamonds;
        int numUpgrades;

        private ParsedRecipe()
        {
            numDiamonds = 0;
            numUpgrades = 0;
        }

        private boolean parseRecipe(InventoryCrafting inv_crafting)
        {
            for (int i = 0; i < inv_crafting.getSizeInventory(); i++) {
                ItemStack stack = inv_crafting.getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                else if (stack.getItem() instanceof DowsingRod) {
                    if (dowsingRodStack != null)
                        return false;
                    dowsingRodStack = stack;
                    dowsingRodItem = (DowsingRod) dowsingRodStack.getItem();
                }
                else if (stack.getItem() == Items.diamond) {
                    numDiamonds++;
                }
                else {
                    return false;
                }
            }
            if (dowsingRodStack == null || numDiamonds == 0) {
                return false;
            }

            int diamonds_per_upgrade = dowsingRodItem.getDiamondsPerUpgrade();
            if (diamonds_per_upgrade == 0 || numDiamonds % diamonds_per_upgrade != 0) {
                return false;
            }

            numUpgrades = numDiamonds / diamonds_per_upgrade;
            if (dowsingRodItem.getNumUpgrades(dowsingRodStack) + numUpgrades
                    > dowsingRodItem.getMaxUpgrades()) {
                return false;
            }

            return true;
        }
    }

    @Override
    public boolean matches(InventoryCrafting inv_crafting, World world)
    {
        return new ParsedRecipe().parseRecipe(inv_crafting);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv_crafting)
    {
        ParsedRecipe parsed = new ParsedRecipe();

        if (!parsed.parseRecipe(inv_crafting))
            return null;

        ItemStack ret_stack = parsed.dowsingRodStack.copy();
        parsed.dowsingRodItem.addUpgrade(ret_stack,  parsed.numUpgrades);
        return ret_stack;
    }

    @Override
    public int getRecipeSize()
    {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

	// XXX unclear about what this does but all the core implementors I looked at
	// had exactly this
	
	@Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return aitemstack;
    }

}
