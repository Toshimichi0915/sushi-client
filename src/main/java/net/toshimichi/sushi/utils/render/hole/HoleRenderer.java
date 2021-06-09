package net.toshimichi.sushi.utils.render.hole;

import net.minecraft.world.World;
import net.toshimichi.sushi.config.data.EspColor;

public interface HoleRenderer {
    void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock);
}
