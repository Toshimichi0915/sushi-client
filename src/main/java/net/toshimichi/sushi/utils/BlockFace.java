package net.toshimichi.sushi.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockFace {
    private final Vec3d pos;
    private final EnumFacing facing;

    public BlockFace(BlockPos pos, EnumFacing facing) {
        this.pos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        this.facing = facing;
    }

    public BlockFace(Vec3d pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(pos);
    }

    public Vec3d getPos() {
        return pos;
    }

    public EnumFacing getFacing() {
        return facing;
    }
}
