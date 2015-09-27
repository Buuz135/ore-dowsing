package org.argon.roderick.minecraft.oredowsing.items;

import java.util.List;

import org.argon.roderick.minecraft.oredowsing.lib.Reference;
import org.argon.roderick.minecraft.oredowsing.lib.cofhDummy;
import org.argon.roderick.minecraft.oredowsing.render.DowsingRodRenderer;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraftforge.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Optional.Interface(modid = "CoFHAPI|energy", iface = "cofh.api.energy.IEnergyContainerItem")
public class DowsingRod extends Item implements IEnergyContainerItem
{
    private static final String BASE_NAME       = "DowsingRod";
    private static final int    DAMAGE_PER_USE  = 1;
    private static final float  RENDER_DURATION = 30.0F;
    private static final int    RF_PER_DAMAGE   = 3000;

    private static final String NBT_RADIUS            = "radius";
    private static final String NBT_TARGET_BLOCK_ID   = "block_id";
    private static final String NBT_TARGET_BLOCK_META = "block_meta";
    private static final String NBT_NUM_UPGRADES      = "num_upgrades";
    
    private final String  name;
    private final Block   forcedTargetBlock;
    private final int     baseSquareRadius;
    private final boolean isChargeable;
    private final int     diamondsPerUpgrade;
    private final int     maxUpgrades;
    public  final Object  ingredientBase;
    public  final Object  ingredientTop;

    public DowsingRod(String parNamePrefix,
            Object parIngredientBase, Object parIngredientTop,
            Block parForcedTargetBlock,
            int parMaxDamage, int parSquareRadius, boolean parIsChargeable,
            int parDiamondsPerUpgrade, int parMaxUpgrades)
    {
        super();

        name               = parNamePrefix + BASE_NAME;
        forcedTargetBlock  = parForcedTargetBlock;
        baseSquareRadius   = parSquareRadius;
        isChargeable       = parIsChargeable;
        diamondsPerUpgrade = parDiamondsPerUpgrade;

        setUnlocalizedName(Reference.MODID + "_" + name);
        setMaxStackSize(1);
        setMaxDamage(parMaxDamage);
        setCreativeTab(CreativeTabs.tabTools);

        maxUpgrades        = parMaxUpgrades;
        ingredientBase     = parIngredientBase;
        ingredientTop      = parIngredientTop;
    }

    public String getName()
    {
    	return name;
    }

    private void initNBT(ItemStack stack)
    {
    	NBTTagCompound tc = stack.getTagCompound();
    	
        if (tc == null) {
            tc = new NBTTagCompound();
            stack.setTagCompound(tc);
        }
        if (forcedTargetBlock != null) {
            forceSetTarget(stack, Block.getIdFromBlock(forcedTargetBlock), 0, null);
        }
        tc.setInteger(NBT_RADIUS, baseSquareRadius);
        tc.setInteger(NBT_NUM_UPGRADES, 0);
    }

    public boolean addUpgrade(ItemStack stack, int num_upgrades)
    {
        if (stack.getTagCompound() == null) {
            initNBT(stack);
        }

        int new_num_upgrades = this.getNumUpgrades(stack) + num_upgrades;
        if (new_num_upgrades > maxUpgrades)
            return false;

        stack.getTagCompound().setInteger(NBT_NUM_UPGRADES, new_num_upgrades);
        stack.getTagCompound().setInteger(NBT_RADIUS, baseSquareRadius + new_num_upgrades);
        return true;
    }

    public boolean addUpgrade(ItemStack stack)
    {
        return this.addUpgrade(stack, 1);
    }

    public int getDiamondsPerUpgrade()
    {
        return diamondsPerUpgrade;
    }

    public int getMaxUpgrades()
    {
        return maxUpgrades;
    }

    public int getNumUpgrades(ItemStack stack)
    {
        if (stack.getTagCompound() == null) {
            initNBT(stack);
        }
        return stack.getTagCompound().getInteger(NBT_NUM_UPGRADES);
    }

    public boolean canUpgrade(ItemStack stack)
    {
        if (stack.getTagCompound() == null) {
            initNBT(stack);
        }
        return this.getNumUpgrades(stack) < this.getMaxUpgrades();
    }

    public ItemStack getTargetStack(ItemStack stack)
    {
        if (stack.getTagCompound() == null) {
            initNBT(stack);
        }
        int block_id = stack.getTagCompound().getInteger(NBT_TARGET_BLOCK_ID);
        int meta     = stack.getTagCompound().getInteger(NBT_TARGET_BLOCK_META);
        return block_id == 0
                ? null
                : new ItemStack(Block.getBlockById(block_id), 1, meta);
    }

    private int getSquareRadius(ItemStack stack)
    {
        if (stack.getTagCompound() == null) {
            initNBT(stack);
        }
        return stack.getTagCompound().getInteger(NBT_RADIUS);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (stack.getTagCompound() == null) {
            initNBT(stack);
        }

        ItemStack target_stack = getTargetStack(stack);
        list.add(String.format(cofhDummy.localize("text.oredowsing.tooltip.0"),
                        (target_stack != null ? target_stack.getDisplayName()
                            : cofhDummy.localize("text.oredowsing.all_ores"))));
        list.add(String.format(cofhDummy.localize("text.oredowsing.tooltip.1"),
                        1+2*getSquareRadius(stack)));
        if (forcedTargetBlock == null) {
            list.add(cofhDummy.localize("text.oredowsing.tooltip.2"));
        }
        if (isChargeable) {
            list.add(cofhDummy.localize("text.oredowsing.tooltip.3"));
        }
        if (this.getNumUpgrades(stack) < maxUpgrades) {
            list.add(String.format(cofhDummy.localize("text.oredowsing.tooltip.4"), diamondsPerUpgrade));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (cofhDummy.isShiftKeyDown()) {
            setTarget(stack, null, world.isRemote ? null : player);
        }
        else {
            divine(stack, world, player);
        }
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (cofhDummy.isShiftKeyDown()) {
            setTarget(stack, world.getBlockState(pos), world.isRemote ? null : player);
        }
        else {
            divine(stack, world, player);
        }
        return true;
    }

    public void setTarget(ItemStack stack, IBlockState targetBlockState, EntityPlayer player)
    {
        if (forcedTargetBlock != null) {
            if (player != null) {
                player.addChatMessage(new ChatComponentText(
                        cofhDummy.localize("text.oredowsing.change_target.no")));
            }
            return;
        }
        forceSetTarget(stack, targetBlockState, player);
    }

    private void forceSetTarget(ItemStack stack, int targetBlockId, int targetBlockMeta, EntityPlayer player)
    {
        if (stack.getTagCompound() == null) {
            initNBT(stack);
        }
        stack.getTagCompound().setInteger(NBT_TARGET_BLOCK_ID,   targetBlockId);
        stack.getTagCompound().setInteger(NBT_TARGET_BLOCK_META, targetBlockMeta);
        if (player != null) {
            player.addChatMessage(new ChatComponentText(String.format(
                    cofhDummy.localize("text.oredowsing.change_target.yes"),
                    (targetBlockId == 0
                        ? cofhDummy.localize("text.oredowsing.all_ores")
                        : new ItemStack(Block.getBlockById(targetBlockId), 1, targetBlockMeta).getDisplayName()))));
        }
    }

    private void forceSetTarget(ItemStack stack, IBlockState targetBlockState, EntityPlayer player)
    {
        if (targetBlockState == null) {
            forceSetTarget(stack, 0, 0, player);
        }
        else {
            Block targetBlock = targetBlockState.getBlock();
            forceSetTarget(stack,
                            Block.getIdFromBlock(targetBlock),
                            targetBlock.getMetaFromState(targetBlockState),
                            player);
        }
    }

    public boolean blockMatches(ItemStack target_stack, ItemStack world_stack)
    {
        return (target_stack != null)
                // detect specific block
        		// XXX use ore dictionary, but only if you can limit it to 
        		// things like oreCopper, not different kinds of planks
                ? target_stack.isItemEqual(world_stack)
                // detect any ore
                : cofhDummy.isOre(world_stack);
    }

    public void divine(ItemStack stack, World world, EntityPlayer player)
    {
        stack.damageItem(DAMAGE_PER_USE, player);

        if (!world.isRemote)
            return;

        ItemStack target_stack = getTargetStack(stack);
        int r = getSquareRadius(stack);
        int x, y, z;
        for (x = (int)player.posX - r; x <= player.posX + r; x++) {
            for (y = (int)player.posY - r; y <= player.posY + r; y++) {
                for (z = (int)player.posZ - r; z <= player.posZ + r; z++) {
                	BlockPos pos       = new BlockPos(x, y, z);
                	IBlockState bstate = world.getBlockState(pos);
                	Block block        = bstate.getBlock();
                	ItemStack is       = new ItemStack(block, 1, block.damageDropped(bstate));
                    if (blockMatches(target_stack, is)) {
                        DowsingRodRenderer.addBlockToHighlight(pos, world, player, RENDER_DURATION);
                	}
                }
            }
        }
    }

    // RF interface --------------------------------------------------------
    
    // XXX test RF recharging with 1.8

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate)
    {
        if (!isChargeable) {
            return 0;
        }

        int cur_damage    = container.getItemDamage();
        int energy_wanted = cur_damage * RF_PER_DAMAGE;
        int energy_taken  = Math.min(energy_wanted, maxReceive);
        int damage_healed = energy_taken / RF_PER_DAMAGE;
        energy_taken = damage_healed * RF_PER_DAMAGE; // adjust for maxReceive % RF_PER_DAMAGE != 0

        if (!simulate) {
            container.setItemDamage(cur_damage - damage_healed);
        }
        //System.out.println("max energy=" + maxReceive
        //      + " energy_taken=" + energy_taken
        //      + " damage_healed=" + damage_healed);
        return energy_taken;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack container)
    {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ItemStack container)
    {
        // Energetic Infuser won't keep offering energy if this is 0
        return isChargeable
                ? container.getItemDamage() * RF_PER_DAMAGE
                : 0;

    }

}
