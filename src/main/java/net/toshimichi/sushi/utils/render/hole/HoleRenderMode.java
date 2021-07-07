package net.toshimichi.sushi.utils.render.hole;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.config.data.Named;
import net.toshimichi.sushi.utils.render.RenderUtils;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

public enum HoleRenderMode implements Named, HoleRenderer {
    @SerializedName("FILL")
    FILL("Fill") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            for (BlockPos pos : info.getBlockPos()) {
                AxisAlignedBB box = world.getBlockState(pos).getBoundingBox(world, pos).offset(pos);
                glDisable(GL_DEPTH_TEST);
                RenderUtils.drawFilled(box, HoleRenderMode.getColor(info, obsidian, bedrock));
                glEnable(GL_DEPTH_TEST);
            }
        }
    };

    private final String name;

    HoleRenderMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    private static Color getColor(HoleInfo info, EspColor obsidian, EspColor bedrock) {
        HoleType holeType = info.getHoleType();
        return holeType.isSafe() ? bedrock.getCurrentColor() : obsidian.getCurrentColor();
    }
}
