package net.toshimichi.sushi.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockFace {
    private final BlockPos blockPos;
    private final EnumFacing facing;

    public BlockFace(BlockPos blockPos, EnumFacing facing) {
        this.blockPos = blockPos;
        this.facing = facing;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public EnumFacing getFacing() {
        return facing;
    }
}
