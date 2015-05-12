package org.argon.roderick.minecraft.oredowsing.items;

import java.util.List;

import org.argon.roderick.minecraft.oredowsing.lib.Reference;
import org.argon.roderick.minecraft.oredowsing.render.DowsingRodRenderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class DowsingRod extends Item
{
	private static final String BASE_NAME       = "DowsingRod";              
	private static final int    DAMAGE_PER_USE  = 1;                         
	private static final float  RENDER_DURATION = 30.0F;                     
                                                                                 
	private Block forcedTargetBlock, curTargetBlock;
	private int baseSquareRadius, curSquareRadius;

    public DowsingRod(String parNamePrefix, Block parForcedTargetBlock, int parMaxDamage, int parSquareRadius)
    {
        super();
		setUnlocalizedName(Reference.MODID + "_" + parNamePrefix + BASE_NAME);
		setTextureName(Reference.MODID + ":" + parNamePrefix + BASE_NAME);
        setMaxStackSize(1);
		setMaxDamage(parMaxDamage);
        setCreativeTab(CreativeTabs.tabTools);
        
        forcedTargetBlock = curTargetBlock  = parForcedTargetBlock; // null for any block
        baseSquareRadius  = curSquareRadius = parSquareRadius;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
    	list.add("Right-click to highlight "
    			+ (curTargetBlock != null ? curTargetBlock.getLocalizedName() : "ores"));
    	list.add("inside a " + (1+2*curSquareRadius) + "-block cube around you.");
    }
    
    //@SideOnly(Side.CLIENT)
    //@Override
    //public void registerIcons(IIconRegister par1IconRegister)
    //{
    //    itemIcon = par1IconRegister.registerIcon(Reference.MODID + ":" + getUnlocalizedName().substring(5));
    //}
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
    	//System.out.println("onItemRightClick remote=" + world.isRemote);
    	divine(stack, world, player);
		return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	//System.out.println("onItemUse remote=" + world.isRemote);
    	divine(stack, world, player);
    	return true;
    }
    
    public void divine(ItemStack stack, World world, EntityPlayer player)
    {
        int x, y, z;
    	//System.out.println("divine() " + player.posX + "," + player.posZ);
    	stack.damageItem(DAMAGE_PER_USE, player);
    	
    	if (world.isRemote) {
    		int ct = 0;
    		for (x = (int)player.posX - curSquareRadius; x <= player.posX + curSquareRadius; x++) {
    			for (y = (int)player.posY - curSquareRadius; y <= player.posY + curSquareRadius; y++) {
    				for (z = (int)player.posZ - curSquareRadius; z <= player.posZ + curSquareRadius; z++) {
    					Block b = world.getBlock(x, y, z);
    					boolean do_mark = false;
    					if (curTargetBlock != null) {
    						if (b == curTargetBlock) {
    							do_mark = true;
    						}
    					}
    					else {
    						// detect any ore
    						for (int id : OreDictionary.getOreIDs(new ItemStack(b))) {
    							String name = OreDictionary.getOreName(id);
    							if (name.startsWith("ore")) { // XXX better way?
    								//System.out.println("hit on id " + id + " (" + OreDictionary.getOreName(id) + ")");
    								do_mark = true;
    								break;
    							}
    						}
    					}
    					if (do_mark) {
    						ct++;
    						DowsingRodRenderer.addBlockToHighlight(new ChunkCoordinates(x, y, z), world, player, RENDER_DURATION);
    					}
    				}
    			}
    		}
    		//System.out.println("hits = " + ct);
    	}
    }

}