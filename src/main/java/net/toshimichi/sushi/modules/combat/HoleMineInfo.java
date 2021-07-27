package net.toshimichi.sushi.modules.combat;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.modules.render.RenderMode;
import net.toshimichi.sushi.utils.render.RenderUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class HoleMineInfo {
    private final BlockPos surroundPos;
    private final BlockPos crystalFloor;
    private final boolean antiSurround;

    public HoleMineInfo(BlockPos surroundPos, BlockPos crystalFloor, boolean antiSurround) {
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

    public void render(World world, RenderMode mode, EspColor color) {
        BlockPos breakingBlock = getSurroundPos();
        AxisAlignedBB box = world.getBlockState(breakingBlock).getBoundingBox(world, breakingBlock)
                .offset(breakingBlock).grow(0.002);
        if (mode == RenderMode.FULL) glDisable(GL_DEPTH_TEST);
        RenderUtils.drawFilled(box, color.getCurrentColor());
        glEnable(GL_DEPTH_TEST);
    }
}
