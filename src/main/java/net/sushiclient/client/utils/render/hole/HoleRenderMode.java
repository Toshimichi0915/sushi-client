package net.sushiclient.client.utils.render.hole;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.World;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.utils.render.RenderUtils;

import java.awt.Color;

public enum HoleRenderMode implements Named, HoleRenderer {
    @SerializedName("FILL")
    FILL("Fill") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            GlStateManager.disableDepth();
            RenderUtils.drawFilled(info.getBox(), HoleRenderMode.getColor(info, obsidian, bedrock));
            GlStateManager.enableDepth();
        }
    },

    @SerializedName("BOTTOM")
    BOTTOM("Bottom") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            GlStateManager.disableDepth();
            double height = info.getBox().maxY - info.getBox().minY;
            RenderUtils.drawFilled(info.getBox().grow(0, -height / 2, 0).offset(0, -height / 2, 0),
                    HoleRenderMode.getColor(info, obsidian, bedrock));
            GlStateManager.enableDepth();
        }
    },

    @SerializedName("OUTLINE")
    OUTLINE("Outline") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            GlStateManager.disableDepth();
            RenderUtils.drawOutline(info.getBox(), HoleRenderMode.getColor(info, obsidian, bedrock), 3);
            GlStateManager.enableDepth();
        }
    },

    @SerializedName("BOTTOM_OUTLINE")
    BOTTOM_OUTLINE("Bottom Outline") {
        @Override
        public void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock) {
            GlStateManager.disableDepth();
            double height = info.getBox().maxY - info.getBox().minY;
            RenderUtils.drawOutline(info.getBox().grow(0, -height / 2, 0).offset(0, -height / 2, 0),
                    HoleRenderMode.getColor(info, obsidian, bedrock), 3);
            GlStateManager.enableDepth();
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
