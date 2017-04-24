package com.buuz135.oredowsing.event;

import com.buuz135.oredowsing.config.OreColor;
import lombok.Data;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public @Data
class BlockHighlight {

    private BlockPos pos;
    private World world;
    private long renderUntilTime;
    private OreColor.Color color;

    public BlockHighlight(BlockPos parPos, World parWorld, long parRenderUntilTime, boolean parShowOreColor) {
        this.pos = parPos;
        this.world = parWorld;
        this.renderUntilTime = parRenderUntilTime;
        this.color = null;
        if (parShowOreColor) {
            OreColor oreColor = OreColor.getOreColorFromBlock(parWorld.getBlockState(parPos).getBlock());
            if (oreColor != null) {
                color = oreColor.getColor();
            }
        }
    }
}
