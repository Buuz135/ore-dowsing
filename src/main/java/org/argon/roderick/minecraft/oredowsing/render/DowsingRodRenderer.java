package org.argon.roderick.minecraft.oredowsing.render;

// Thanks to Vazkii for the code on which the following was based.

/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Mar 24, 2014, 7:02:37 PM (GMT)]
 */
//package vazkii.botania.client.core.handler;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.argon.roderick.minecraft.oredowsing.lib.Constants;
import org.lwjgl.opengl.GL11;

// XXX store colors as Color objects not ints

public final class DowsingRodRenderer {
    private static Hashtable<BlockPos, BlockToHighlight> blocksToHighlight = new Hashtable<BlockPos, BlockToHighlight>();

    private static Hashtable<String, Integer> blockColor = new Hashtable<String, Integer>();
    static {

        // vanilla

        addOreColorWithNether("Coal",           0x333333);
        addOreColorWithNether("Diamond",        0x6ae7ea);
        addOreColorWithNether("Emerald",        0x21761e);
        addOreColorWithNether("Gold",           0x958e06);
        addOreColorWithNether("Iron",           0xD3AD8D);
        addOreColorWithNether("Lapis",          0x2c3ba6);
        addOreColorWithNether("Quartz",         0xaf9d90);
        addOreColorWithNether("Redstone",       0xa20600);

        // Thaumcraft

        addOreColorWithNether("Amber",          0xB88100);
        addOreColorWithNether("Cinnabar",       0x470100);
        addOreColorWithNether("InfusedAir",     0x837E26);
        addOreColorWithNether("InfusedFire",    0x852000);
        addOreColorWithNether("InfusedWater",   0x094C76);
        addOreColorWithNether("InfusedEarth",   0x104E00);
        addOreColorWithNether("InfusedOrder",   0x786268);
        addOreColorWithNether("InfusedEntropy", 0x2D2B34);

        // other mods

        addOreColorWithNether("CertusQuartz",   0x87A4C3);
        addOreColorWithNether("Copper",         0x8C4900);
        addOreColorWithNether("Lead",           0x5E6B98);
        addOreColorWithNether("Mithril",        0x549298);
        addOreColorWithNether("Nickel",         0xA3A27D);
        addOreColorWithNether("Osmium",         0x435E7D);
        addOreColorWithNether("Platinum",       0x1F609B);
        addOreColorWithNether("Silver",         0xA5B8BE);
        addOreColorWithNether("Tin",            0x97B7DB);

    }

    public static void addOreColorWithNether(String baseName, int rgb) {
        addOreColor("ore"       + baseName, rgb);
        addOreColor("oreNether" + baseName, rgb);
    }

    public static void addOreColor(String name, int rgb) {
        blockColor.put(name, rgb);
    }

    private static class BlockToHighlight {
            BlockPos pos;
            World world;
            long renderUntilTime;
            int rgb;

            public BlockToHighlight(BlockPos parPos, World parWorld, long parRenderUntilTime) {
                pos = parPos;
                world = parWorld;
                renderUntilTime = parRenderUntilTime;

                Block block = world.getBlockState(pos).getBlock();
                //int metadata = world.getBlockMetadata(pos);
                //int metadata = block.getDamageValue(world, pos);
                int ore_ids[] = OreDictionary.getOreIDs(new ItemStack(block));
                rgb = -1;
                for (int i = 0; i < ore_ids.length; i++) {
                	String ore_name = OreDictionary.getOreName(ore_ids[i]);
                    if (blockColor.containsKey(ore_name)) {
                        rgb = (Integer) blockColor.get(ore_name);
                        break;
                    }
                }
                if (rgb == -1) {
                    //System.out.println("no color for " + ore_name + " from " + block);
                }

            }
    }

    public static void addBlockToHighlight(BlockPos parPos, World parWorld, EntityPlayer parPlayer, float parRenderDuration) {
    	//System.out.println("highlight " + parPos);
        blocksToHighlight.put(parPos,
                new DowsingRodRenderer.BlockToHighlight(parPos, parWorld,
                            parWorld.getTotalWorldTime() + Math.round(Constants.TICKS_PER_SEC * parRenderDuration)
                        )
        );
    }

    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event) {
        if (blocksToHighlight.isEmpty()) {
            return;
        }

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableBlend();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(1F, 1F, 1F);

        Enumeration<BlockPos> e = blocksToHighlight.keys();
        while (e.hasMoreElements()) {
            BlockPos keyPos = e.nextElement();
            BlockToHighlight blockToHighlight = blocksToHighlight.get(keyPos);
            long cur_time = blockToHighlight.world.getTotalWorldTime();

            if (blockToHighlight.renderUntilTime < cur_time
                    || blockToHighlight.world.provider.getDimensionId() != Minecraft.getMinecraft().theWorld.provider.getDimensionId()
                    // XXX handle any replacement rather than just air
                    || blockToHighlight.world.isAirBlock(keyPos)
                    ) {
                blocksToHighlight.remove(blockToHighlight.pos);
            }
            else {
                renderBlockOutlineAt(blockToHighlight.pos,
                        blockToHighlight.rgb != -1
                            ? blockToHighlight.rgb
                            : Color.HSBtoRGB(cur_time % 200 / 200F, 0.6F, 1F));
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    private void renderBlockOutlineAt(BlockPos pos, int color) {
        renderBlockOutlineAt(pos, color, 1F);
    }

    private void renderBlockOutlineAt(BlockPos pos, int color, float thickness) {
        GlStateManager.pushMatrix();

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        GlStateManager.translate(
        		pos.getX() - player.posX,
        		pos.getY() - player.posY,
        		pos.getZ() - player.posZ);

        Color colorRGB = new Color(color);

        World world = Minecraft.getMinecraft().theWorld;
        Block block = world.getBlockState(pos).getBlock();
       drawWireframe : {
            if (block != null) {
                AxisAlignedBB axis;

                //if(block instanceof IWireframeAABBProvider)
                //  axis = ((IWireframeAABBProvider) block).getWireframeAABB(world, pos.getX(), pos.getY(), pos.getZ());
                //else
                    axis = block.getSelectedBoundingBox(world, pos);

                if (axis == null)
                    break drawWireframe;

                axis = new AxisAlignedBB(
                                axis.minX - pos.getX(),
                                axis.minY - pos.getY(),
                                axis.minZ - pos.getZ(),
                                axis.maxX - pos.getX(),
                                axis.maxY - pos.getY(),
                                axis.maxZ - pos.getZ());

                GlStateManager.color(colorRGB.getRed()/255F, colorRGB.getGreen()/255F, colorRGB.getBlue()/255F, 255);
                GL11.glLineWidth(thickness);
                renderBlockOutline(axis);

                GlStateManager.color(colorRGB.getRed()/255F, colorRGB.getGreen()/255F, colorRGB.getBlue()/255F, 64);
                GL11.glLineWidth(thickness + 1F);
                renderBlockOutline(axis);
            }
        }

        GlStateManager.popMatrix();
    }

    private void renderBlockOutline(AxisAlignedBB aabb) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        double ix = aabb.minX;
        double iy = aabb.minY;
        double iz = aabb.minZ;
        double ax = aabb.maxX;
        double ay = aabb.maxY;
        double az = aabb.maxZ;
        
        //System.out.println("outline " + ix + "," + iy + "," + iz + " - " + ax + "," + ay + "," + az);

        wr.startDrawing(GL11.GL_LINES);

        wr.addVertex(ix, iy, iz);
        wr.addVertex(ix, ay, iz);

        wr.addVertex(ix, ay, iz);
        wr.addVertex(ax, ay, iz);

        wr.addVertex(ax, ay, iz);
        wr.addVertex(ax, iy, iz);

        wr.addVertex(ax, iy, iz);
        wr.addVertex(ix, iy, iz);

        wr.addVertex(ix, iy, az);
        wr.addVertex(ix, ay, az);

        wr.addVertex(ix, iy, az);
        wr.addVertex(ax, iy, az);

        wr.addVertex(ax, iy, az);
        wr.addVertex(ax, ay, az);

        wr.addVertex(ix, ay, az);
        wr.addVertex(ax, ay, az);

        wr.addVertex(ix, iy, iz);
        wr.addVertex(ix, iy, az);

        wr.addVertex(ix, ay, iz);
        wr.addVertex(ix, ay, az);

        wr.addVertex(ax, iy, iz);
        wr.addVertex(ax, iy, az);

        wr.addVertex(ax, ay, iz);
        wr.addVertex(ax, ay, az);

        tess.draw();
    }
    
}
