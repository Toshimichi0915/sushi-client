package net.toshimichi.sushi.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RotationUtils {

    public static double[][] points(Entity entity) {
        AxisAlignedBB box = entity.getEntityBoundingBox();
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double avgX = (box.minX + box.maxX) / 2;
        double avgY = (box.minY + box.maxY) / 2;
        double avgZ = (box.minZ + box.maxZ) / 2;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;
        double[][] points = new double[14][3];
        points[0] = new double[]{minX, minY, minZ};
        points[1] = new double[]{avgX, minY, minZ};
        points[2] = new double[]{maxX, minY, minZ};
        points[3] = new double[]{minX, avgY, minZ};
        points[4] = new double[]{minX, maxY, minZ};
        points[5] = new double[]{minX, minY, avgZ};
        points[6] = new double[]{minX, minY, maxZ};
        points[7] = new double[]{avgX, avgY, minZ};
        points[8] = new double[]{maxX, maxY, minZ};
        points[9] = new double[]{avgX, minY, avgZ};
        points[10] = new double[]{maxX, minY, maxZ};
        points[11] = new double[]{minX, avgY, avgZ};
        points[12] = new double[]{minX, maxY, maxZ};
        points[13] = new double[]{maxX, maxY, maxZ};
        return points;
    }

    public static double[][] points(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        double[][] points = new double[14][3];
        points[0] = new double[]{x, y, z};
        points[1] = new double[]{x + 0.5, y, z};
        points[2] = new double[]{x + 1, y, z};
        points[3] = new double[]{x, y + 0.5, z};
        points[4] = new double[]{x, y + 1, z};
        points[5] = new double[]{x, y, z + 0.5};
        points[6] = new double[]{x, y, z + 1};
        points[7] = new double[]{x + 0.5, y + 0.5, z};
        points[8] = new double[]{x + 1, y + 1, z};
        points[9] = new double[]{x + 0.5, y, z + 0.5};
        points[10] = new double[]{x + 1, y, z + 1};
        points[11] = new double[]{x, y + 0.5, z + 0.5};
        points[12] = new double[]{x, y + 1, z + 1};
        points[13] = new double[]{x + 1, y + 1, z + 1};
        return points;
    }

    public static Vec3d closest(Vec3d origin, double distance, double[][] points) {
        Vec3d vec = origin;
        double dist = distance;
        for (double[] point : points) {
            Vec3d newVec = new Vec3d(point[0], point[1], point[3]);
            double newDist = origin.squareDistanceTo(newVec);
            if (newDist <= dist) {
                vec = newVec;
                dist = newDist;
            }
        }
        return vec.subtract(origin);
    }

    public static Vec3d raytrace(Vec3d origin, double distance, double[][] points, World world, Entity entity) {
        Vec3d vec = origin;
        double dist = distance;
        for (double[] point : points) {
            Vec3d newVec = new Vec3d(point[0], point[1], point[3]);
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

    public static Vec3d raytrace(Vec3d origin, double distance, double[][] points, World world, BlockFace face) {
        BlockPos block = face.getBlockPos();
        EnumFacing facing = face.getFacing();
        Vec3d vec = origin;
        double dist = distance;
        for (double[] point : points) {
            Vec3d newVec = new Vec3d(point[0], point[1], point[3]);
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
