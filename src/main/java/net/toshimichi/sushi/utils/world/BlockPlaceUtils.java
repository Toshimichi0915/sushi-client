package net.toshimichi.sushi.utils.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class BlockPlaceUtils {

    private static boolean searchRecursively(World world, BlockPos target, BlockPos current, EnumFacing exclude, int real, int distance,
                                             HashSet<BlockPos> closed, List<BlockPlaceInfo> result, Function<BlockPos, Boolean> access) {
        if (real > distance) return false;
        if (!BlockUtils.canPlace(world, new BlockPlaceInfo(current, null))) return false;
        if (closed.contains(current)) return false;
        if (!access.apply(current)) return false;

        BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(world, current);
        if (info != null) {
            result.add(info);
            return true;
        }
        ArrayList<BlockNode> nodes = new ArrayList<>(5);
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == exclude) continue;
            BlockPos pos = current.offset(facing);
            closed.add(pos);
            double fake = target.distanceSq(pos);
            nodes.add(new BlockNode(pos, facing, real, fake));
        }
        nodes.sort(null);
        for (BlockNode node : nodes) {
            boolean found = searchRecursively(world, target, node.pos, node.facing.getOpposite(), real + 1, distance, closed, result, access);
            if (found) {
                result.add(new BlockFace(node.pos, node.facing.getOpposite()).toBlockPlaceInfo(world));
                return true;
            }
        }
        return false;
    }

    public static List<BlockPlaceInfo> search(World world, BlockPos target, int distance, Set<BlockPos> closed, Function<BlockPos, Boolean> access) {
        ArrayList<BlockPlaceInfo> result = new ArrayList<>();
        boolean found = searchRecursively(world, target, target, null, 0, distance, new HashSet<>(closed), result, access);
        if (found) return result;
        else return null;
    }

    public static List<BlockPlaceInfo> search(World world, BlockPos target, int distance) {
        return search(world, target, distance, new HashSet<>(), p -> true);
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
