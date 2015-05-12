package org.argon.roderick.minecraft.oredowsing.render;

/* Thanks to Vazkii for most of the code which follows. */

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
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

//import vazkii.botania.api.BotaniaAPI;
//import vazkii.botania.api.item.IExtendedWireframeCoordinateListProvider;
//import vazkii.botania.api.item.IWireframeCoordinateListProvider;
//import vazkii.botania.api.wand.ICoordBoundItem;
//import vazkii.botania.api.wand.IWireframeAABBProvider;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public final class DowsingRodRenderer {
	private final static int TICKS_PER_SEC = 20; // XXX get from elsewhere
	private static Hashtable blocksToHighlight = new Hashtable();

	private static int getTickCounter() {
		return MinecraftServer.getServer().getTickCounter();
	}
	
	private static class BlockToHighlight {
            ChunkCoordinates pos;
            World world;
            EntityPlayer player;
            int renderUntilTick;
            
            public BlockToHighlight(ChunkCoordinates parPos, World parWorld, EntityPlayer parPlayer, int parRenderUntilTick) {
            	pos = parPos;
            	world = parWorld;
            	player = parPlayer;
            	renderUntilTick = parRenderUntilTick;
            }
	}

    public static void addBlockToHighlight(ChunkCoordinates parPos, World parWorld, EntityPlayer parPlayer, float parRenderDuration) {
    	blocksToHighlight.put(parPos,
    			new DowsingRodRenderer.BlockToHighlight(parPos, parWorld, parPlayer,
    						getTickCounter() + Math.round(TICKS_PER_SEC * parRenderDuration)
    					)
    	);
    }

	@SubscribeEvent
	public void onWorldRenderLast(RenderWorldLastEvent event) {
    	if (blocksToHighlight.isEmpty()) {
    		return;
    	}

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);

		Tessellator.renderingWorldRenderer = false;

		int tick = getTickCounter();
		int color = Color.HSBtoRGB(tick % 200 / 200F, 0.6F, 1F);

        Enumeration e = blocksToHighlight.keys();
        while (e.hasMoreElements()) {
        	ChunkCoordinates keyPos = (ChunkCoordinates) e.nextElement();
        	BlockToHighlight blockToHighlight = (BlockToHighlight) blocksToHighlight.get(keyPos);
            Block block = blockToHighlight.world.getBlock(blockToHighlight.pos.posX, blockToHighlight.pos.posY, blockToHighlight.pos.posZ);

        	if (blockToHighlight.renderUntilTick < tick
        			// XXX better way?  does this even work?
        			|| blockToHighlight.world.provider.dimensionId != Minecraft.getMinecraft().theWorld.provider.dimensionId
        			// XXX handle any replacement rather than just air
        			|| block == Blocks.air
        			) {
        		blocksToHighlight.remove(blockToHighlight.pos);
        	}
        	else {
        		renderBlockOutlineAt(blockToHighlight.pos, color);
        	}
        }

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	private void renderBlockOutlineAt(ChunkCoordinates pos, int color) {
		renderBlockOutlineAt(pos, color, 1F);
	}

	private void renderBlockOutlineAt(ChunkCoordinates pos, int color, float thickness) {
		GL11.glPushMatrix();
		GL11.glTranslated(pos.posX - RenderManager.renderPosX, pos.posY - RenderManager.renderPosY, pos.posZ - RenderManager.renderPosZ + 1);
		Color colorRGB = new Color(color);
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 255);

		World world = Minecraft.getMinecraft().theWorld;
		Block block = world.getBlock(pos.posX, pos.posY, pos.posZ);
		drawWireframe : {
			if(block != null) {
				AxisAlignedBB axis;

				//if(block instanceof IWireframeAABBProvider)
				//	axis = ((IWireframeAABBProvider) block).getWireframeAABB(world, pos.posX, pos.posY, pos.posZ);
				//else
					axis = block.getSelectedBoundingBoxFromPool(world, pos.posX, pos.posY, pos.posZ);

				if(axis == null)
					break drawWireframe;

				axis.minX -= pos.posX;
				axis.maxX -= pos.posX;
				axis.minY -= pos.posY;
				axis.maxY -= pos.posY;
				axis.minZ -= pos.posZ + 1;
				axis.maxZ -= pos.posZ + 1;

				GL11.glScalef(1F, 1F, 1F);

				GL11.glLineWidth(thickness);
				renderBlockOutline(axis);

				GL11.glLineWidth(thickness + 3F);
				GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 64);
				renderBlockOutline(axis);
			}
		}

		GL11.glPopMatrix();
	}

	private void renderBlockOutline(AxisAlignedBB aabb) {
		Tessellator tessellator = Tessellator.instance;

		double ix = aabb.minX;
		double iy = aabb.minY;
		double iz = aabb.minZ;
		double ax = aabb.maxX;
		double ay = aabb.maxY;
		double az = aabb.maxZ;

		tessellator.startDrawing(GL11.GL_LINES);
		tessellator.addVertex(ix, iy, iz);
		tessellator.addVertex(ix, ay, iz);

		tessellator.addVertex(ix, ay, iz);
		tessellator.addVertex(ax, ay, iz);

		tessellator.addVertex(ax, ay, iz);
		tessellator.addVertex(ax, iy, iz);

		tessellator.addVertex(ax, iy, iz);
		tessellator.addVertex(ix, iy, iz);

		tessellator.addVertex(ix, iy, az);
		tessellator.addVertex(ix, ay, az);

		tessellator.addVertex(ix, iy, az);
		tessellator.addVertex(ax, iy, az);

		tessellator.addVertex(ax, iy, az);
		tessellator.addVertex(ax, ay, az);

		tessellator.addVertex(ix, ay, az);
		tessellator.addVertex(ax, ay, az);

		tessellator.addVertex(ix, iy, iz);
		tessellator.addVertex(ix, iy, az);

		tessellator.addVertex(ix, ay, iz);
		tessellator.addVertex(ix, ay, az);

		tessellator.addVertex(ax, iy, iz);
		tessellator.addVertex(ax, iy, az);

		tessellator.addVertex(ax, ay, iz);
		tessellator.addVertex(ax, ay, az);

		tessellator.draw();
	}
}