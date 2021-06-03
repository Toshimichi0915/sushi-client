package net.toshimichi.sushi.utils.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface BlockPlacer {
    List<BlockPlaceInfo> getProcess(World world, BlockPos origin, BlockPos target, int distance);
}
