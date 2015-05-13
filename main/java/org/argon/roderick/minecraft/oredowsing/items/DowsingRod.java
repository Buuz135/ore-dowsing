package org.argon.roderick.minecraft.oredowsing.items;

import java.util.List;

import org.argon.roderick.minecraft.oredowsing.lib.Reference;
import org.argon.roderick.minecraft.oredowsing.render.DowsingRodRenderer;
import org.lwjgl.input.Keyboard;

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

public class DowsingRod extends Item
{
	private static final String BASE_NAME       = "DowsingRod";              
	private static final int    DAMAGE_PER_USE  = 1;                         
	private static final float  RENDER_DURATION = 30.0F;                     

	private static final String NBT_RADIUS		= "radius";
	private static final String NBT_TARGET_BLOCK_ID = "block_id";
                                                                                 
	private Block forcedTargetBlock; // null for any ore
	private int baseSquareRadius;

    public DowsingRod(String parNamePrefix, Block parForcedTargetBlock, int parMaxDamage, int parSquareRadius)
    {
        super();
		setUnlocalizedName(Reference.MODID + "_" + parNamePrefix + BASE_NAME);
		setTextureName(Reference.MODID + ":" + parNamePrefix + BASE_NAME);
        setMaxStackSize(1);
		setMaxDamage(parMaxDamage);
        setCreativeTab(CreativeTabs.tabTools);
        
        forcedTargetBlock = parForcedTargetBlock;
        baseSquareRadius  = parSquareRadius;
    }
    
    private void initNBT(ItemStack stack)
    {
    	if (stack.stackTagCompound == null) {
    		stack.stackTagCompound= new NBTTagCompound();
    	}
        if (forcedTargetBlock != null) {
        	forceSetTargetBlock(stack, forcedTargetBlock, null);
        }
    	stack.stackTagCompound.setInteger(NBT_RADIUS, baseSquareRadius);
    }
    
    private Block getTargetBlock(ItemStack stack)
    {
    	if (stack.stackTagCompound == null) {
    		initNBT(stack);
    	}
    	int target_block_id = stack.stackTagCompound.getInteger(NBT_TARGET_BLOCK_ID);
    	return target_block_id == 0 ? null : Block.getBlockById(target_block_id);
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

    	Block b = getTargetBlock(stack);
    	list.add("Right-click to highlight " + (b != null ? b.getLocalizedName() : "ores"));
    	list.add("inside a size " + (1+2*getSquareRadius(stack)) + " cube around you.");
    	if (forcedTargetBlock == null) {
    		list.add("Shift-right-click to change target block.");
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
    		setTargetBlock(stack, null, world.isRemote ? null : player);
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
    		setTargetBlock(stack, world.getBlock(x, y, z),
    				world.isRemote ? null : player);
    	}
    	else {
    		divine(stack, world, player);
    	}
    	return true;
    }
    
	public void setTargetBlock(ItemStack stack, Block targetBlock, EntityPlayer player)
	{
		if (forcedTargetBlock != null) {
			if (player != null) {
				player.addChatMessage(new ChatComponentText("This item doesn't allow changing the target block"));
			}
			return;
		}
		forceSetTargetBlock(stack, targetBlock, player);
	}
	
    private void forceSetTargetBlock(ItemStack stack, Block targetBlock, EntityPlayer player)
    {
    	if (stack.stackTagCompound == null) {
    		initNBT(stack);
    	}
        stack.stackTagCompound.setInteger(NBT_TARGET_BLOCK_ID,
        		targetBlock == null ? 0 : Block.getIdFromBlock(targetBlock));
    	if (player != null) {
    		player.addChatMessage(new ChatComponentText("Target set to "
    				+ (targetBlock == null ? "all ores" : targetBlock.getLocalizedName())));
    	}
    }
    
    	// XXX does this have to use stack?
    public boolean blockMatches(ItemStack stack, Block worldBlock)
    {
    	Block targetBlock = getTargetBlock(stack);

    	// detect specific block, but use ore dictionary
/*
    	if (curTargetOreIDs.length > 0) {
    		int[] worldOreIDs = OreDictionary.getOreIDs(new ItemStack(worldBlock));
    		for (int targ_id : curTargetOreIDs) {
    			for (int world_id : worldOreIDs) {
    				if (targ_id == world_id) {
    					return true;
    				}
    			}
    		}
    		return false;
    	}
*/
    	if (false) {}
    	
    	// detect specific block, no ore dictionary
    	else if (targetBlock != null) {
    		return OreDictionary.itemMatches(
    				new ItemStack(worldBlock),
    				new ItemStack(targetBlock),
    				true);
    	}
    	
    	// detect any ore
        for (int id : OreDictionary.getOreIDs(new ItemStack(worldBlock))) {
        	String name = OreDictionary.getOreName(id);
        	if (name.startsWith("ore")) { // XXX better way?
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
        int x, y, z;
    	int ct = 0;
    	for (x = (int)player.posX - r; x <= player.posX + r; x++) {
    		for (y = (int)player.posY - r; y <= player.posY + r; y++) {
    			for (z = (int)player.posZ - r; z <= player.posZ + r; z++) {
    				if (blockMatches(stack, world.getBlock(x, y, z))) {
    					ct++;
    					DowsingRodRenderer.addBlockToHighlight(new ChunkCoordinates(x, y, z), world, player, RENDER_DURATION);
    				}
    			}
    		}
    	}
    }
    
}
