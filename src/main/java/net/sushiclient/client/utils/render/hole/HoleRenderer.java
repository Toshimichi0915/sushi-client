package net.sushiclient.client.utils.render.hole;

import net.minecraft.world.World;
import net.sushiclient.client.config.data.EspColor;

public interface HoleRenderer {
    void render(World world, HoleInfo info, EspColor obsidian, EspColor bedrock);
}
