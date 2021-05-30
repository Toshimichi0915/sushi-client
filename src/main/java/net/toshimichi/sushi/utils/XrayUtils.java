package net.toshimichi.sushi.utils;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.Map;

public class XrayUtils {

    private static final HashMap<Block, BlockVisibility> visibilityMap = new HashMap<>();
    private static boolean enabled;

    public static BlockVisibility getBlockVisibility(Block block) {
        return visibilityMap.getOrDefault(block, BlockVisibility.VISIBLE);
    }

    public static void setBlockVisibility(Block block, BlockVisibility visibility) {
        if (block == Blocks.AIR) return;
        if (visibility == BlockVisibility.VISIBLE) visibilityMap.remove(block);
        else visibilityMap.put(block, visibility);
    }

    private static void load() {
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
    }

    public static void apply() {
        enabled = true;
        load();
    }

    public static void reset() {
        enabled = false;
        visibilityMap.clear();
        load();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static Map<Block, BlockVisibility> getAll() {
        return ImmutableMap.copyOf(visibilityMap);
    }
}
