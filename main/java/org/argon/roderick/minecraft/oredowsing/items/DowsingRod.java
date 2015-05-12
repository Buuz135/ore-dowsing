package org.argon.roderick.minecraft.oredowsing.items;

import java.util.List;

import org.argon.roderick.minecraft.oredowsing.lib.Reference;
import org.argon.roderick.minecraft.oredowsing.render.DowsingRodRenderer;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class DowsingRod extends Item
{
	private static final String BASE_NAME       = "DowsingRod";              
	private static final int    DAMAGE_PER_USE  = 1;                         
	private static final float  RENDER_DURATION = 30.0F;                     
                                                                                 
	private Block forcedTargetBlock, curTargetBlock;
	private int[] curTargetOreIDs = {};
	private int baseSquareRadius, curSquareRadius;

    public DowsingRod(String parNamePrefix, Block parForcedTargetBlock, int parMaxDamage, int parSquareRadius)
    {
        super();
		setUnlocalizedName(Reference.MODID + "_" + parNamePrefix + BASE_NAME);
		setTextureName(Reference.MODID + ":" + parNamePrefix + BASE_NAME);
        setMaxStackSize(1);
		setMaxDamage(parMaxDamage);
        setCreativeTab(CreativeTabs.tabTools);
        
        forcedTargetBlock = parForcedTargetBlock; // null for any block
        if (forcedTargetBlock != null) {
        	forceSetCurTargetBlock(forcedTargetBlock, null);
        }

        baseSquareRadius  = curSquareRadius = parSquareRadius;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
    	list.add("Right-click to highlight "
    			+ (curTargetBlock != null ? curTargetBlock.getLocalizedName() : "ores"));
    	list.add("inside a size " + (1+2*curSquareRadius) + " cube around you.");
    }
    
    private boolean isShifted()
    {
    	return Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
    	if (isShifted()) {
    		setCurTargetBlock(null, world.isRemote ? null : player);
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
    		return setCurTargetBlock(world.getBlock(x, y, z),
    				world.isRemote ? null : player);
    	}
    	else {
    		divine(stack, world, player);
    		return true;
    	}
    }
    
	public boolean setCurTargetBlock(Block targetBlock, EntityPlayer player)
	{
		if (forcedTargetBlock != null) {
			if (player != null) {
				player.addChatMessage(new ChatComponentText("This item doesn't allow changing the target block"));
			}
			return false;
		}
		forceSetCurTargetBlock(targetBlock, player);
		return true;
	}
	
    private void forceSetCurTargetBlock(Block targetBlock, EntityPlayer player)
    {
    	// XXX support nether ores?  e.g. oreIron matching oreNetherIron
    	curTargetBlock  = targetBlock;
    	curTargetOreIDs = (targetBlock == null ? new int[0] : OreDictionary.getOreIDs(new ItemStack(targetBlock)));
    	if (player != null) {
    		player.addChatMessage(new ChatComponentText("Target set to "
    				+ (targetBlock == null ? "all ores" : targetBlock.getLocalizedName())));
    	}
    }
    
    public boolean blockMatches(Block worldBlock)
    {
    	// detect specific block, but use ore dictionary
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
    	
    	// detect specific block, no ore dictionary
    	else if (curTargetBlock != null) {
    		return OreDictionary.itemMatches(
    				new ItemStack(worldBlock),
    				new ItemStack(curTargetBlock),
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
    	if (!world.isRemote)
    		return;
    	
    	stack.damageItem(DAMAGE_PER_USE, player);

        int x, y, z;
    	int ct = 0;
    	for (x = (int)player.posX - curSquareRadius; x <= player.posX + curSquareRadius; x++) {
    		for (y = (int)player.posY - curSquareRadius; y <= player.posY + curSquareRadius; y++) {
    			for (z = (int)player.posZ - curSquareRadius; z <= player.posZ + curSquareRadius; z++) {
    				if (blockMatches(world.getBlock(x, y, z))) {
    					ct++;
    					DowsingRodRenderer.addBlockToHighlight(new ChunkCoordinates(x, y, z), world, player, RENDER_DURATION);
    				}
    			}
    		}
    	}
    }

}