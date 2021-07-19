package net.toshimichi.sushi.utils.render.hole;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.utils.world.BlockUtils;

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

    private int distance() {
        Vec3d playerPos = Minecraft.getMinecraft().player.getPositionVector();
        int total = 0;
        for (BlockPos pos : blockPos) {
            total += playerPos.squareDistanceTo(BlockUtils.toVec3d(pos));
        }
        return total / blockPos.length;
    }

    @Override
    public int compareTo(HoleInfo o) {
        return Integer.compare(distance(), o.distance());
    }
}
