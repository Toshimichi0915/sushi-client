package net.toshimichi.sushi.modules.combat;

import net.minecraft.util.math.BlockPos;

public class HoleMineInfo {
    private final BlockPos surroundPos;
    private final BlockPos crystalFloor;
    private final boolean antiSurround;

    HoleMineInfo(BlockPos surroundPos, BlockPos crystalFloor, boolean antiSurround) {
        this.surroundPos = surroundPos;
        this.antiSurround = antiSurround;
        this.crystalFloor = crystalFloor;
    }

    public BlockPos getSurroundPos() {
        return surroundPos;
    }

    public BlockPos getCrystalFloor() {
        return crystalFloor;
    }

    public boolean isAntiSurround() {
        return antiSurround;
    }
}
