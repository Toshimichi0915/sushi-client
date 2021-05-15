package net.toshimichi.sushi.utils;

import net.minecraft.util.math.BlockPos;

public class BlockPlaceInfo {
    private final BlockPos blockPos;
    private final BlockFace blockFace;

    public BlockPlaceInfo(BlockPos blockPos, BlockFace blockFace) {
        this.blockPos = blockPos;
        this.blockFace = blockFace;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }
}
