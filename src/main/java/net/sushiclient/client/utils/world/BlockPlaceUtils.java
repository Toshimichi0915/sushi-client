package net.sushiclient.client.utils.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;

public class BlockPlaceUtils {

    private static void searchRecursively(World world, BlockPos target, BlockPos current, EnumFacing exclude, int real, int distance,
                                          boolean parent, Collection<List<BlockNode>> contexts, List<BlockNode> context,
                                          HashSet<BlockPos> closed, List<BlockPlaceInfo> result, Function<BlockPos, Boolean> access,
                                          PlaceOptions[] options) {
        if (real > distance) return;
        if (!BlockUtils.canPlace(world, new BlockPlaceInfo(current, null), options)) return;
        if (closed.contains(current)) return;
        if (!access.apply(current)) return;
        closed.add(current);

        BlockPlaceInfo info = BlockUtils.findBlockPlaceInfo(world, current, options);
        if (info != null) {
            result.add(info);
            return;
        }
        ArrayList<BlockNode> nodes = new ArrayList<>(5);
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == exclude) continue;
            BlockPos pos = current.offset(facing);
            double fake = target.distanceSq(pos);
            nodes.add(new BlockNode(pos, facing, real, fake));
        }
        nodes.sort(null);
        for (BlockNode node : nodes) {
            ArrayList<BlockNode> newContext = new ArrayList<>(context);
            newContext.add(node);
            contexts.add(newContext);
        }

        if (!parent) return;
        int count = 0;
        while (!contexts.isEmpty()) {
            for (List<BlockNode> ctx : new HashSet<>(contexts)) {
                contexts.remove(ctx);
                BlockNode last = ctx.get(ctx.size() - 1);
                searchRecursively(world, target, last.pos, last.facing.getOpposite(), count, distance, false, contexts, ctx, closed, result, access, options);
                if (!result.isEmpty()) {
                    ctx.remove(last);
                    Collections.reverse(ctx);
                    for (BlockNode node : ctx) {
                        result.add(new BlockFace(node.pos, node.facing.getOpposite()).toBlockPlaceInfo(world));
                    }
                    return;
                }
            }
            count++;
        }
    }

    public static List<BlockPlaceInfo> search(World world, BlockPos target, int distance, Set<
            BlockPos> closed, Function<BlockPos, Boolean> access, PlaceOptions... options) {
        ArrayList<BlockPlaceInfo> result = new ArrayList<>();
        searchRecursively(world, target, target, null, 0, distance, true, new HashSet<>(), new ArrayList<>(), new HashSet<>(closed), result, access, options);
        if (!result.isEmpty()) return result;
        else return null;
    }

    public static List<BlockPlaceInfo> search(World world, BlockPos target, int distance, PlaceOptions... options) {
        return search(world, target, distance, new HashSet<>(), p -> true, options);
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
