package net.sushiclient.client.utils.render.hole;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Arrays;

public class HoleInfo implements Comparable<HoleInfo> {
    private final BlockPos[] blockPos;
    private final AxisAlignedBB box;
    private final HoleType holeType;

    public HoleInfo(BlockPos[] blockPos, AxisAlignedBB box, HoleType holeType) {
        this.blockPos = blockPos;
        this.box = box;
        this.holeType = holeType;
    }

    public BlockPos[] getBlockPos() {
        return blockPos;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    public HoleType getHoleType() {
        return holeType;
    }

    private double distance() {
        Vec3d playerPos = Minecraft.getMinecraft().player.getPositionVector();
        double total = 0;
        for (BlockPos pos : blockPos) {
            total += playerPos.squareDistanceTo(BlockUtils.toVec3d(pos));
        }
        return total / blockPos.length;
    }

    private boolean compareBlockPos(BlockPos[] arr1, BlockPos[] arr2) {
        if (arr1.length != arr2.length) return false;
        for (int i = 0; i < arr1.length; i++) {
            BlockPos pos1 = arr1[i];
            BlockPos pos2 = arr2[i];
            if (pos1.getX() != pos2.getX() || pos1.getY() != pos2.getY() || pos1.getZ() != pos2.getZ()) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HoleInfo holeInfo = (HoleInfo) o;

        if (!compareBlockPos(blockPos, holeInfo.blockPos)) return false;
        if (box != null ? !box.equals(holeInfo.box) : holeInfo.box != null) return false;
        return holeType == holeInfo.holeType;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(blockPos);
        result = 31 * result + (box != null ? box.hashCode() : 0);
        result = 31 * result + (holeType != null ? holeType.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(HoleInfo o) {
        return Double.compare(distance(), o.distance());
    }
}
