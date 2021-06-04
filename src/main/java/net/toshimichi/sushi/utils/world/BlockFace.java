package net.toshimichi.sushi.utils.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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

    public BlockPlaceInfo toBlockPlaceInfo(World world) {
        BlockPos offset = getBlockPos().offset(facing);
        IBlockState blockState = world.getBlockState(getBlockPos());
        AxisAlignedBB box = blockState.getBoundingBox(world, offset);
        box = box.offset(-box.minX, -box.minY, -box.minZ).offset(new Vec3d(facing.getDirectionVec()).scale(0.5));
        Vec3d center = box.getCenter();
        return new BlockPlaceInfo(offset, new BlockFace(center, facing));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockFace blockFace = (BlockFace) o;

        if (pos != null ? !pos.equals(blockFace.pos) : blockFace.pos != null) return false;
        return facing == blockFace.facing;
    }

    @Override
    public int hashCode() {
        int result = pos != null ? pos.hashCode() : 0;
        result = 31 * result + (facing != null ? facing.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BlockFace{" +
                "pos=" + pos +
                ", facing=" + facing +
                '}';
    }
}
