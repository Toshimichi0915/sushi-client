package net.toshimichi.sushi.utils.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BlockPlaceUtils {

    private static boolean searchRecursively(World world, BlockPos target, BlockPos current, EnumFacing exclude, int real, int distance,
                                             HashSet<BlockPos> closed, List<BlockPlaceInfo> result) {
        if (real > distance) return false;
        if (!BlockUtils.isAir(world, current)) return false;
        if (!world.checkNoEntityCollision(world.getBlockState(current).getBoundingBox(world, current))) return false;
        BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(world, current);
        if (info != null) {
            result.add(info);
            return true;
        }
        ArrayList<BlockNode> nodes = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == exclude) continue;
            BlockPos pos = current.offset(facing);
            if (closed.contains(pos)) continue;
            double fake = target.distanceSq(pos);
            nodes.add(new BlockNode(pos, facing, real, fake));
        }
        nodes.sort(null);
        for (BlockNode node : nodes) {
            closed.add(node.pos);
            boolean found = searchRecursively(world, target, node.pos, node.facing.getOpposite(), real + 1, distance, closed, result);
            if (found) {
                result.add(new BlockFace(node.pos, node.facing.getOpposite()).toBlockPlaceInfo(world));
                return true;
            }
        }
        return false;
    }

    public static List<BlockPlaceInfo> search(World world, BlockPos target, int distance) {
        ArrayList<BlockPlaceInfo> result = new ArrayList<>();
        searchRecursively(world, target, target, null, 0, distance, new HashSet<>(), result);
        return result;
    }

    private static class BlockNode implements Comparable<BlockNode> {
        BlockPos pos;
        EnumFacing facing;
        int real;
        double fake;

        public BlockNode(BlockPos pos, EnumFacing facing, int real, double fake) {
            this.pos = pos;
            this.facing = facing;
            this.real = real;
            this.fake = fake;
        }

        public double total() {
            return real * real + fake;
        }

        @Override
        public int compareTo(BlockNode o) {
            return Double.compare(total(), o.total());
        }
    }
}
