package net.toshimichi.sushi.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class RotationUtils {

    public static List<Vec3d> points(AxisAlignedBB box, EnumFacing facing) {
        int xOffset = facing == null ? 0 : facing.getXOffset();
        int yOffset = facing == null ? 0 : facing.getYOffset();
        int zOffset = facing == null ? 0 : facing.getZOffset();
        double minX = xOffset == 1 ? box.maxX : box.minX;
        double minY = yOffset == 1 ? box.maxY : box.minY;
        double minZ = zOffset == 1 ? box.maxZ : box.minZ;
        double maxX = xOffset == -1 ? box.minX : box.maxX;
        double maxY = yOffset == -1 ? box.minY : box.maxY;
        double maxZ = zOffset == -1 ? box.minZ : box.maxZ;
        double avgX = (minX + maxX) / 2;
        double avgY = (minY + maxY) / 2;
        double avgZ = (minZ + maxZ) / 2;
        return Arrays.asList(
                new Vec3d(minX, minY, minZ), new Vec3d(avgX, minY, minZ), new Vec3d(maxX, minY, minZ),
                new Vec3d(minX, avgY, minZ), new Vec3d(minX, maxY, minZ), new Vec3d(minX, minY, avgZ),
                new Vec3d(minX, minY, maxZ), new Vec3d(avgX, avgY, minZ), new Vec3d(maxX, maxY, minZ),
                new Vec3d(avgX, minY, avgZ), new Vec3d(maxX, minY, maxZ), new Vec3d(minX, avgY, avgZ),
                new Vec3d(minX, maxY, maxZ), new Vec3d(maxX, maxY, maxZ));
    }

    public static Vec3d closest(Vec3d origin, double distance, List<Vec3d> points) {
        Vec3d vec = origin;
        double dist = distance;
        for (Vec3d newVec : points) {
            double newDist = origin.squareDistanceTo(newVec);
            if (newDist <= dist) {
                vec = newVec;
                dist = newDist;
            }
        }
        return vec.subtract(origin);
    }

    public static Vec3d raytrace(Vec3d origin, double distance, List<Vec3d> points, World world, Entity entity) {
        Vec3d vec = origin;
        double dist = distance;
        for (Vec3d newVec : points) {
            double newDist = origin.squareDistanceTo(newVec);
            RayTraceResult result = world.rayTraceBlocks(origin, newVec);
            if (result == null || entity != result.entityHit) continue;
            if (newDist <= dist) {
                vec = newVec;
                dist = newDist;
            }
        }
        return vec.subtract(origin);
    }

    public static Vec3d raytrace(Vec3d origin, double distance, List<Vec3d> points, World world, BlockFace face) {
        BlockPos block = face.getBlockPos();
        EnumFacing facing = face.getFacing();
        Vec3d vec = origin;
        double dist = distance;
        for (Vec3d newVec : points) {
            double newDist = origin.squareDistanceTo(newVec);
            RayTraceResult result = world.rayTraceBlocks(origin, newVec);
            if (result == null || !block.equals(result.getBlockPos()) ||
                    (facing != null && facing != result.sideHit)) continue;
            if (newDist <= dist) {
                vec = newVec;
                dist = newDist;
            }
        }
        return vec.subtract(origin);
    }
}
