package net.toshimichi.sushi.utils.render.hole;

import net.minecraft.util.math.BlockPos;

public class HoleInfo {
    private final BlockPos[] blockPos;
    private final HoleType holeType;

    public HoleInfo(BlockPos[] blockPos, HoleType holeType) {
        this.blockPos = blockPos;
        this.holeType = holeType;
    }

    public BlockPos[] getBlockPos() {
        return blockPos;
    }

    public HoleType getHoleType() {
        return holeType;
    }
}
