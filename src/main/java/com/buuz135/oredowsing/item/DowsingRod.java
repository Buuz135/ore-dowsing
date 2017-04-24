package com.buuz135.oredowsing.item;


import com.buuz135.oredowsing.OreDowsing;
import com.buuz135.oredowsing.config.OreColor;
import com.buuz135.oredowsing.config.OreDowsingConfig;
import com.buuz135.oredowsing.event.BlockHighlight;
import com.buuz135.oredowsing.event.RenderWorldEvent;
import com.buuz135.oredowsing.util.StringUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDigging;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class DowsingRod extends ItemEnergy {

    private static final String NBT_RADIUS = "radius";
    private static final String NBT_TIER = "tier";
    private static final String NBT_TARGET_BLOCK_ID = "block_id";
    private static final String NBT_TARGET_BLOCK_METADATA = "block_metadata";
    private static final String NBT_TIER_UPGRADE = "tier_upgrade";
    private static final int MIN_TIER = 1;
    private static final int MIN_RAD = 7;
    private static final int MAX_RAD = 29;
    private static final int MAX_TIER = 4;

    private boolean creative;


    public DowsingRod(String name, int stackSize, int maxPoxer, int transfer,boolean creative) {
        super(name, stackSize, maxPoxer, transfer);
        this.creative = creative;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        checkForNBT(stack);
    }

    @Override
    public boolean updateItemStackNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey(NBT_TIER)) nbt.setInteger(NBT_TIER, creative ? Integer.MAX_VALUE : MIN_TIER);
        if (!nbt.hasKey(NBT_RADIUS)) nbt.setInteger(NBT_RADIUS, creative ? 30 : MIN_RAD);
        if (!nbt.hasKey(NBT_TIER_UPGRADE)) nbt.setInteger(NBT_TIER_UPGRADE, 0);
        return super.updateItemStackNBT(nbt);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        int tier = getTier(stack);
        int radius = getRadius(stack);
        tooltip.add(StringUtil.getStringWithColor("Tier: ", TextFormatting.GOLD) + StringUtil.getStringWithColor(String.valueOf(tier), TextFormatting.YELLOW));
        tooltip.add(StringUtil.getStringWithColor("Radius: ", TextFormatting.GOLD) + StringUtil.getStringWithColor(String.valueOf(radius), TextFormatting.YELLOW));
        if (getTier(stack)<MAX_TIER)tooltip.add(StringUtil.getStringWithColor("Tier progress: ", TextFormatting.GOLD) + StringUtil.getStringWithColor(String.valueOf(getProgressTier(stack)), TextFormatting.YELLOW) +
                StringUtil.getStringWithColor("%", TextFormatting.WHITE));
    }

    private int getRadius(ItemStack stack) {
        checkForNBT(stack);
        int extra = 0;
        for (int i = 0; i < stack.getEnchantmentTagList().tagCount();++i){
            NBTTagCompound compound = stack.getEnchantmentTagList().getCompoundTagAt(i);
            if (Enchantment.getEnchantmentByID(compound.getShort("id")) instanceof EnchantmentDigging){
                extra = OreDowsingConfig.efficiencyIncrease*compound.getShort("lvl");
                break;
            }
        }
        return OreDowsingConfig.minSize + extra;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        checkForNBT(stack);
        useRod(playerIn, worldIn, stack);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }



    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = worldIn.getBlockState(pos);
        if (getTier(stack) < MAX_TIER && canUse(stack, 100)) {
            if (StringUtil.isOre(state.getBlock(), state)) {
                OreColor color = OreColor.getOreColorFromBlock(state.getBlock());
                if (color != null) {
                    if (!worldIn.isRemote) {
                        increaseTierProgress(stack, color.getValue());
                        worldIn.setBlockToAir(pos);
                        if (getCurrentUpgradeLevel(stack) >= getAmountNeededForNextTier(stack)){
                            setTier(stack,getTier(stack)+1);
                            setTierUpgrade(stack, 0);
                        }
                    } else {
                        Random random = new Random();
                        for (int i = 0; i < 10; ++i)
                            worldIn.spawnParticle(EnumParticleTypes.CLOUD, true, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                    random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5);
                        worldIn.playSound(player, pos, new SoundEvent(new ResourceLocation("minecraft:block.wood_button.click_on")), SoundCategory.BLOCKS, 1, 1);
                    }
                    return EnumActionResult.SUCCESS;
                }
            }

        }
        return EnumActionResult.PASS;
    }

    private void setTierUpgrade(ItemStack stack, int i) {
        stack.getTagCompound().setInteger(NBT_TIER_UPGRADE,0);
    }

    public int getTier(ItemStack stack) {
        checkForNBT(stack);
        return stack.getTagCompound().getInteger(NBT_TIER);
    }

    public void setTier(ItemStack stack, int i) {
        stack.getTagCompound().setInteger(NBT_TIER, i);
    }

    public void checkForNBT(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound() == null ? new NBTTagCompound() : stack.getTagCompound();
        updateItemStackNBT(compound);
        stack.setTagCompound(compound);
    }

    public void useRod(EntityPlayer player, World world, ItemStack stack) {
        if (!world.isRemote) return;
        int tier = getTier(stack);
        int radius = getRadius(stack);
        switch (tier) {
            case 1:
                if (canUse(stack, 200)) {
                    player.sendMessage(new TextComponentTranslation("text.ores_nearby").setStyle(new Style().setColor(TextFormatting.GRAY)));
                    List<Item> items = new ArrayList<Item>();
                    for (BlockPos pos : getNerbyOres(new BlockPos(player.posX, player.posY, player.posZ), radius, world)) {
                        Item item = Item.getItemFromBlock(world.getBlockState(pos).getBlock());
                        if (!items.contains(item)) {
                            player.sendMessage(new TextComponentString(StringUtil.getStringWithColor(" - ", TextFormatting.GRAY) + new TextComponentTranslation(new ItemStack(Block.getBlockFromItem(item), 1, Block.getBlockFromItem(item).damageDropped(world.getBlockState(pos))).getUnlocalizedName() + ".name").setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText()));
                            items.add(item);
                        }
                    }
                    if (items.isEmpty()) player.sendMessage(new TextComponentString(StringUtil.getStringWithColor(" - None",TextFormatting.GRAY)));
                }
                break;
            case 2:
                if (canUse(stack, 600)) {
                    player.sendMessage(new TextComponentTranslation("text.ores_nearby").setStyle(new Style().setColor(TextFormatting.GRAY)));
                    HashMap<Item, BlockPos> items = new HashMap<>();
                    for (BlockPos pos : getNerbyOres(new BlockPos(player.posX, player.posY, player.posZ), radius, world)) {
                        Item item = Item.getItemFromBlock(world.getBlockState(pos).getBlock());
                        if (!items.containsKey(item)) {
                            items.put(item, pos);
                        } else {
                            if (player.getDistanceSqToCenter(pos) <= player.getDistanceSqToCenter(items.get(item))) {
                                items.replace(item, pos);
                            }
                        }
                    }
                    for (Item item : items.keySet()) {
                        BlockPos pos = items.get(item);
                        player.sendMessage(new TextComponentString(StringUtil.getStringWithColor(" - ", TextFormatting.GRAY) +
                                new TextComponentTranslation(new ItemStack(Block.getBlockFromItem(item), 1, Block.getBlockFromItem(item).damageDropped(world.getBlockState(pos))).getUnlocalizedName() + ".name").setStyle(new Style().setColor(TextFormatting.GRAY)).getFormattedText() +
                                StringUtil.getStringWithColor(" (Distance: " + ((int) Math.ceil(player.getDistance(pos.getX(), pos.getY(), pos.getZ()))) + ")", TextFormatting.GRAY)));
                    }
                }
                break;
            case 3:
                if (canUse(stack, 2000)) {
                    for (BlockPos pos : getNerbyOres(new BlockPos(player.posX, player.posY, player.posZ), radius, world)) {
                        RenderWorldEvent.blockHighlightHashtable.put(pos, new BlockHighlight(pos, world, OreDowsingConfig.Rendering.renderTime * 20 + world.getTotalWorldTime(), false));
                    }
                }
                break;
            default:
                if (creative || canUse(stack, 4000)) {
                    for (BlockPos pos : getNerbyOres(new BlockPos(player.posX, player.posY, player.posZ), radius, world)) {
                        RenderWorldEvent.blockHighlightHashtable.put(pos, new BlockHighlight(pos, world, OreDowsingConfig.Rendering.renderTime * 20 + world.getTotalWorldTime(), true));
                    }
                }
                break;
        }
    }

    public List<BlockPos> getNerbyOres(BlockPos player, int radius, World world) {
        List<BlockPos> list = new ArrayList<>();
        for (int x = player.getX() - radius; x <= player.getX() + radius; x++) {
            for (int y = player.getY() - radius; y <= player.getY() + radius; y++) {
                for (int z = player.getZ() - radius; z <= player.getZ() + radius; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState bstate = world.getBlockState(pos);
                    Block block = bstate.getBlock();
                    if (StringUtil.isOre(block, bstate)) list.add(pos);
                }
            }
        }
        return list;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return super.isBookEnchantable(stack, book);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment instanceof EnchantmentDigging;
    }

    public int getAmountNeededForNextTier(ItemStack stack) {
        return (int) Math.pow(2, 5 + getTier(stack));
    }

    public int getCurrentUpgradeLevel(ItemStack stack) {
        checkForNBT(stack);
        return stack.getTagCompound().getInteger(NBT_TIER_UPGRADE);
    }

    public int getProgressTier(ItemStack stack) {
        return (int) ((getCurrentUpgradeLevel(stack) / (double) getAmountNeededForNextTier(stack)) * 100);
    }

    public void increaseTierProgress(ItemStack stack, int value) {
        stack.getTagCompound().setInteger(NBT_TIER_UPGRADE, getCurrentUpgradeLevel(stack) + value);
    }

    public boolean canUse(ItemStack stack, int energy) {
        IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
        int removed = 0;
        if (storage != null) removed = storage.extractEnergy(energy, false);
        return removed > 0;
    }
}
