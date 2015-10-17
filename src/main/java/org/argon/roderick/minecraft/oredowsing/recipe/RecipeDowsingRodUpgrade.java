package org.argon.roderick.minecraft.oredowsing.recipe;

import org.argon.roderick.minecraft.oredowsing.items.DowsingRod;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeDowsingRodUpgrade implements IRecipe {

    private class ParsedRecipe {
        ItemStack dowsingRodStack;
        DowsingRod dowsingRodItem;
        int numUpgrades;

        private ParsedRecipe()
        {
            this.numUpgrades = 0;
        }

        private boolean parseRecipe(InventoryCrafting inv_crafting)
        {
            int i;

            // find dowsing rod

            int dowsing_rod_slot = -1;
            for (i = 0; i < inv_crafting.getSizeInventory(); i++) {
                ItemStack stack = inv_crafting.getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                else if (stack.getItem() instanceof DowsingRod) {
                    dowsing_rod_slot = i;
                    this.dowsingRodStack = stack;
                    break;
                }
            }
            if (dowsing_rod_slot == -1) {
                return false;
            }

            // count upgrade items in crafting grid

            this.dowsingRodItem = (DowsingRod) this.dowsingRodStack.getItem();
            int items_per_upgrade = this.dowsingRodItem.getItemsPerUpgrade(this.dowsingRodStack);
            if (items_per_upgrade < 1) {
                return false;
            }
            ItemStack upgrade_stack = this.dowsingRodItem.getUpgradeItemStack(this.dowsingRodStack);
            int num_items = 0;

            for (i = 0; i < inv_crafting.getSizeInventory(); i++) {
                if (i == dowsing_rod_slot) {
                    continue;
                }
                ItemStack stack = inv_crafting.getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                else if (stack.isItemEqual(upgrade_stack)) {
                    num_items++;
                }
                else {
                    return false;
                }
            }
            if (num_items == 0 || num_items % items_per_upgrade != 0) {
                return false;
            }

            this.numUpgrades = num_items / items_per_upgrade;
            return this.dowsingRodItem.canUpgrade(this.dowsingRodStack, this.numUpgrades);
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
        parsed.dowsingRodItem.addUpgrade(ret_stack, parsed.numUpgrades);
        return ret_stack;
    }

    @Override
    public int getRecipeSize()
    {
        return 9; // XXX hardcode
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

}
