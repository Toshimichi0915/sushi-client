package net.toshimichi.sushi.utils;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Collection;
import java.util.function.Consumer;

public class SearchUtils {

    public static void find(Chunk chunk, Collection<Block> targets, Consumer<BlockPos> consumer) {
        World world = chunk.getWorld();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = new BlockPos((chunk.x << 4) + x, y, (chunk.z << 4) + z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (targets.contains(block)) consumer.accept(pos);
                }
            }
        }
    }
}
