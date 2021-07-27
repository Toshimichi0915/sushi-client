package net.toshimichi.sushi.utils.render.hole;

import com.google.gson.annotations.SerializedName;
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
            glDisable(GL_DEPTH_TEST);
            RenderUtils.drawFilled(info.getBox(), HoleRenderMode.getColor(info, obsidian, bedrock));
            glEnable(GL_DEPTH_TEST);
        }
    },

    @SerializedName("BOTTOM")
    BOTTOM("Bottom") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            glDisable(GL_DEPTH_TEST);
            double height = info.getBox().maxY - info.getBox().minY;
            RenderUtils.drawFilled(info.getBox().grow(0, -height / 2, 0).offset(0, -height / 2, 0),
                    HoleRenderMode.getColor(info, obsidian, bedrock));
            glEnable(GL_DEPTH_TEST);
        }
    },

    @SerializedName("OUTLINE")
    OUTLINE("Outline") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            glDisable(GL_DEPTH_TEST);
            RenderUtils.drawOutline(info.getBox(), HoleRenderMode.getColor(info, obsidian, bedrock), 3);
            glEnable(GL_DEPTH_TEST);
        }
    },

    @SerializedName("BOTTOM_OUTLINE")
    BOTTOM_OUTLINE("Bottom Outline") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            glDisable(GL_DEPTH_TEST);
            double height = info.getBox().maxY - info.getBox().minY;
            RenderUtils.drawOutline(info.getBox().grow(0, -height / 2, 0).offset(0, -height / 2, 0),
                    HoleRenderMode.getColor(info, obsidian, bedrock), 3);
            glEnable(GL_DEPTH_TEST);
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
