package net.toshimichi.sushi.utils;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class AStarBlockPlacer implements BlockPlacer {
    private final HashSet<BlockPos> closed = new HashSet<>();

    private boolean isOpened(BlockPos pos) {
        return !closed.contains(pos);
    }

    private void close(BlockPos pos) {
        closed.add(pos);
    }

    private void searchRecursively(World world, BlockPos current, BlockPos target, int distance, int cost, EnumFacing exclude, List<BlockFace> list) {
        if (cost > distance) return;
        Vec3d targetCenter = BlockUtils.toVec3d(target).add(0.5, 0.5, 0.5);
        BlockNode[] nodes = new BlockNode[5];
        int index = -1;
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == exclude) continue;
            index++;
            BlockPos offset = current.offset(facing);
            Vec3d center = BlockUtils.toVec3d(offset).add(0.5, 0.5, 0.5);
            if (closed.contains(offset)) continue;
            if (!BlockUtils.isAir(world, offset)) {
                closed.add(offset);
                continue;
            }
            nodes[index] = new BlockNode(new BlockFace(offset, facing), center.distanceTo(targetCenter) + cost + 1);
        }
        Arrays.sort(nodes, Comparator.comparingDouble(node -> node.cost));
        for (BlockNode node : nodes) {
            list.add(node.blockFace);
            if (node.blockFace.getBlockPos().equals(target)) return;
            searchRecursively(world, node.blockFace.getBlockPos(), target, distance, cost + 1, node.blockFace.getFacing().getOpposite(), list);
            if (list.get(list.size() - 1).getBlockPos().equals(target)) return;
            list.remove(node.blockFace);
        }
    }

    @Override
    public List<BlockPlaceInfo> getProcess(World world, BlockPos origin, BlockPos target, int distance) {
        ArrayList<BlockFace> faces = new ArrayList<>();
        searchRecursively(world, origin, target, distance, 0, null, faces);
        if (!faces.get(faces.size() - 1).getBlockPos().equals(target)) return null;
        ArrayList<BlockPlaceInfo> result = new ArrayList<>();
        for (BlockFace face : faces) {
            result.add(face.toBlockPlaceInfo(world));
        }
        return result;
    }

    private static class BlockNode {
        final BlockFace blockFace;
        final double cost;

        BlockNode(BlockFace blockFace, double cost) {
            this.blockFace = blockFace;
            this.cost = cost;
        }
    }
}
