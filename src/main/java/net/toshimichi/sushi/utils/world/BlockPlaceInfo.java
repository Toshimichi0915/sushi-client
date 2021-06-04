package net.toshimichi.sushi.utils.world;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockPlaceInfo that = (BlockPlaceInfo) o;

        if (blockPos != null ? !blockPos.equals(that.blockPos) : that.blockPos != null) return false;
        return blockFace != null ? blockFace.equals(that.blockFace) : that.blockFace == null;
    }

    @Override
    public int hashCode() {
        int result = blockPos != null ? blockPos.hashCode() : 0;
        result = 31 * result + (blockFace != null ? blockFace.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BlockPlaceInfo{" +
                "blockPos=" + blockPos +
                ", blockFace=" + blockFace +
                '}';
    }
}
