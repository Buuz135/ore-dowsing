package org.argon.roderick.minecraft.oredowsing.items;

import java.util.List;

import org.argon.roderick.minecraft.oredowsing.lib.Reference;
import org.argon.roderick.minecraft.oredowsing.render.DowsingRodRenderer;
import org.lwjgl.input.Keyboard;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class DowsingRod extends Item implements IEnergyContainerItem
{
	private static final String BASE_NAME       = "DowsingRod";              
	private static final int    DAMAGE_PER_USE  = 1;                         
	private static final float  RENDER_DURATION = 30.0F;                     
	private static final int    RF_PER_DAMAGE   = 3000;

	private static final String NBT_RADIUS		= "radius";
	private static final String NBT_TARGET_BLOCK_ID = "block_id";
	private static final String NBT_TARGET_BLOCK_METADATA = "block_metadata";
                                                                                 
	private final Block   forcedTargetBlock; // null for any ore
	private final int     baseSquareRadius;
	private final boolean isChargeable;

    public DowsingRod(String parNamePrefix, Block parForcedTargetBlock, int parMaxDamage, int parSquareRadius, boolean parIsChargeable)
    {
        super();
		setUnlocalizedName(Reference.MODID + "_" + parNamePrefix + BASE_NAME);
		setTextureName(Reference.MODID + ":" + parNamePrefix + BASE_NAME);
        setMaxStackSize(1);
		setMaxDamage(parMaxDamage);
        setCreativeTab(CreativeTabs.tabTools);
        
        forcedTargetBlock = parForcedTargetBlock;
        baseSquareRadius  = parSquareRadius;
        isChargeable      = parIsChargeable;
    }
    
    private void initNBT(ItemStack stack)
    {
    	if (stack.stackTagCompound == null) {
    		stack.stackTagCompound= new NBTTagCompound();
    	}
        if (forcedTargetBlock != null) {
        	forceSetTarget(stack, forcedTargetBlock, 0, null);
        }
    	stack.stackTagCompound.setInteger(NBT_RADIUS, baseSquareRadius);
    }
    
    private ItemStack getTargetStack(ItemStack stack)
    {
    	if (stack.stackTagCompound == null) {
    		initNBT(stack);
    	}
    	int block_id = stack.stackTagCompound.getInteger(NBT_TARGET_BLOCK_ID);
    	int metadata = stack.stackTagCompound.getInteger(NBT_TARGET_BLOCK_METADATA);
    	return block_id == 0
    			? null
    			: new ItemStack(Block.getBlockById(block_id), 1, metadata);
    }

    private int getSquareRadius(ItemStack stack)
    {
    	if (stack.stackTagCompound == null) {
    		initNBT(stack);
    	}
    	return stack.stackTagCompound.getInteger(NBT_RADIUS);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
    	if (stack.stackTagCompound == null) {
    		initNBT(stack);
    	}

    	ItemStack target_stack = getTargetStack(stack);
    	list.add("Right-click to highlight " + (target_stack != null ? target_stack.getDisplayName() : "ores"));
    	list.add("inside a size " + (1+2*getSquareRadius(stack)) + " cube around you.");
    	if (forcedTargetBlock == null) {
    		list.add("Shift-right-click to change target block.");
    	}
    	if (isChargeable) {
    		list.add("Charge with RF to repair.");
    	}
    }
    
    private boolean isShifted()
    {
    	return Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
    	if (isShifted()) {
    		setTarget(stack, null, 0, world.isRemote ? null : player);
    	}
    	else {
    		divine(stack, world, player);
    	}
		return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	if (isShifted()) {
    		setTarget(stack, world.getBlock(x, y, z),
    				world.getBlockMetadata(x, y, z),
    				world.isRemote ? null : player);
    	}
    	else {
    		divine(stack, world, player);
    	}
    	return true;
    }
    
	public void setTarget(ItemStack stack, Block targetBlock, int metadata, EntityPlayer player)
	{
		if (forcedTargetBlock != null) {
			if (player != null) {
				player.addChatMessage(new ChatComponentText("This item doesn't allow changing the target block"));
			}
			return;
		}
		forceSetTarget(stack, targetBlock, metadata, player);
	}
	
    private void forceSetTarget(ItemStack stack, Block targetBlock, int metadata, EntityPlayer player)
    {
    	if (stack.stackTagCompound == null) {
    		initNBT(stack);
    	}
        stack.stackTagCompound.setInteger(NBT_TARGET_BLOCK_ID,
        		targetBlock == null ? 0 : Block.getIdFromBlock(targetBlock));
        stack.stackTagCompound.setInteger(NBT_TARGET_BLOCK_METADATA, metadata);
    	if (player != null) {
    		player.addChatMessage(new ChatComponentText("Target set to "
    				+ (targetBlock == null
    					? "all ores"
    					: new ItemStack(targetBlock, 1, metadata).getDisplayName())));
    	}
    }
    
    private boolean nameIsOre(String name)
    {
    	return name.startsWith("ore"); // is there a better way?
    }

    public boolean blockMatches(ItemStack stack, ItemStack world_stack)
    {
    	ItemStack target_stack = getTargetStack(stack);
    	int[] target_ore_ids = OreDictionary.getOreIDs(target_stack);

    	// detect specific block, but use ore dictionary
    	if (target_ore_ids.length > 0) {
    		int[] world_ore_ids = OreDictionary.getOreIDs(world_stack);
    		for (int targ_id : target_ore_ids) {
    			for (int world_id : world_ore_ids) {
    				if (targ_id == world_id) {
    					return true;
    				}
    			}
    		}
    		return false;
    	}
    	
    	// detect specific block, no ore dictionary
    	else if (target_stack != null) {
    		return OreDictionary.itemMatches(world_stack, target_stack, true);
    	}
    	
    	// detect any ore
        for (int id : OreDictionary.getOreIDs(world_stack)) {
        	if (nameIsOre(OreDictionary.getOreName(id))) {
        		return true;
        	}
        }
        return false;
    }

    public void divine(ItemStack stack, World world, EntityPlayer player)
    {
    	stack.damageItem(DAMAGE_PER_USE, player);

    	if (!world.isRemote)
    		return;
    	
    	int r = getSquareRadius(stack);
    	int ct = 0;
        int x, y, z;
    	for (x = (int)player.posX - r; x <= player.posX + r; x++) {
    		for (y = (int)player.posY - r; y <= player.posY + r; y++) {
    			for (z = (int)player.posZ - r; z <= player.posZ + r; z++) {
    				if (blockMatches(stack,
    						new ItemStack(world.getBlock(x, y, z), 1,
    								world.getBlockMetadata(x, y, z)))) {
    					ct++;
    					DowsingRodRenderer.addBlockToHighlight(new ChunkCoordinates(x, y, z), world, player, RENDER_DURATION);
    				}
    			}
    		}
    	}
    }
    
    // ---------------------------------------------------------------------
    
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
    	//		+ " energy_taken=" + energy_taken
    	//		+ " damage_healed=" + damage_healed);
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
