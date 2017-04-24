package com.buuz135.oredowsing.event;

import com.buuz135.oredowsing.config.OreColor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class RenderWorldEvent {

    public static Hashtable<BlockPos, BlockHighlight> blockHighlightHashtable = new Hashtable<BlockPos, BlockHighlight>();

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (blockHighlightHashtable.isEmpty()) return;
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        Enumeration<BlockPos> set = blockHighlightHashtable.keys();
        while (set.hasMoreElements()) {
            BlockPos pos = set.nextElement();
            BlockHighlight blockHighlight = blockHighlightHashtable.get(pos);
            long time = blockHighlight.getWorld().getTotalWorldTime();
            if (blockHighlight.getRenderUntilTime() < time || blockHighlight.getWorld().provider.getDimension() != Minecraft.getMinecraft().world.provider.getDimension() || blockHighlight.getWorld().isAirBlock(pos)) {
                blockHighlightHashtable.remove(pos);
                continue;
            }
            renderBlockOutlineAt(pos, event.getPartialTicks(), blockHighlight.getColor(), 1, time);
        }
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }


    private void renderBlockOutlineAt(BlockPos pos, float partialticks, OreColor.Color color, float thickness, long time) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Vec3d playerPos = player.getPositionEyes(partialticks);
        GL11.glTranslated(pos.getX() - playerPos.xCoord, pos.getY() - playerPos.yCoord + player.getEyeHeight(), pos.getZ() - playerPos.zCoord);

        World world = Minecraft.getMinecraft().world;
        Block block = world.getBlockState(pos).getBlock();
        AxisAlignedBB axis;
        axis = block.getSelectedBoundingBox(world.getBlockState(pos), world, pos);

        axis = new AxisAlignedBB(
                axis.minX - pos.getX(),
                axis.minY - pos.getY(),
                axis.minZ - pos.getZ(),
                axis.maxX - pos.getX(),
                axis.maxY - pos.getY(),
                axis.maxZ - pos.getZ());

        if (color != null)
            GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 64);
        else {
            Color c = new Color(Color.HSBtoRGB(time % 200 / 200F, 0.6F, 1F));
            GlStateManager.color(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 64);
        }
        GL11.glLineWidth(thickness + 1F);
        renderBlockOutline(axis);

        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glPopAttrib();
    }

    private void renderBlockOutline(AxisAlignedBB aabb) {
        Tessellator tess = Tessellator.getInstance();
        double xa = aabb.minX;
        double xb = aabb.maxX;
        double ya = aabb.minY;
        double yb = aabb.maxY;
        double za = aabb.minZ;
        double zb = aabb.maxZ;

        tess.getBuffer().begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(xa, ya, za).endVertex();
        tess.getBuffer().pos(xa, yb, za).endVertex();
        tess.getBuffer().pos(xb, yb, za).endVertex();
        tess.getBuffer().pos(xb, ya, za).endVertex();
        tess.getBuffer().pos(xa, ya, za).endVertex();

        tess.getBuffer().pos(xa, ya, zb).endVertex();
        tess.getBuffer().pos(xa, yb, zb).endVertex();
        tess.getBuffer().pos(xb, yb, zb).endVertex();
        tess.getBuffer().pos(xb, ya, zb).endVertex();
        tess.getBuffer().pos(xa, ya, zb).endVertex();
        tess.draw();

        tess.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        tess.getBuffer().pos(xa, yb, za).endVertex();
        tess.getBuffer().pos(xa, yb, zb).endVertex();

        tess.getBuffer().pos(xb, ya, za).endVertex();
        tess.getBuffer().pos(xb, ya, zb).endVertex();

        tess.getBuffer().pos(xb, yb, za).endVertex();
        tess.getBuffer().pos(xb, yb, zb).endVertex();

        tess.draw();

    }
}
