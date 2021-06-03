package net.toshimichi.sushi.utils.render;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.WorldLoadEvent;
import net.toshimichi.sushi.events.world.ChunkLoadEvent;
import net.toshimichi.sushi.events.world.ChunkUnloadEvent;
import net.toshimichi.sushi.utils.world.WorldEventAdapter;

import java.io.Closeable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SearchMap implements Closeable {

    private final Collection<Block> targets;
    private final HashSet<BlockPos> results = new HashSet<>();
    private final IWorldEventListener listener;
    private World world;

    public SearchMap(Collection<Block> targets) {
        this.targets = targets;
        this.listener = new SearchListener();
        EventHandlers.register(this);

        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) return;
        this.world = world;
        world.addEventListener(listener);

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        int renderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                Chunk chunk = world.getChunk(player.chunkCoordX + x, player.chunkCoordZ + z);
                if (!chunk.isLoaded()) continue;
                SearchUtils.find(chunk, targets, results::add);
            }
        }
    }

    public Set<BlockPos> getResults() {
        return ImmutableSet.copyOf(results);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onChunkLoad(ChunkLoadEvent e) {
        SearchUtils.find(e.getChunk(), targets, results::add);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onChunkUnload(ChunkUnloadEvent e) {
        SearchUtils.find(e.getChunk(), targets, results::remove);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldLoad(WorldLoadEvent e) {
        if (world != null) world.removeEventListener(listener);
        world = e.getClient();
        if (world != null) world.addEventListener(listener);
    }

    @Override
    public void close() {
        EventHandlers.unregister(this);
        if (world != null) world.removeEventListener(listener);
    }

    private class SearchListener extends WorldEventAdapter {
        @Override
        public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            if (!worldIn.equals(world)) return;
            results.remove(pos);
            if (targets.contains(newState.getBlock())) results.add(pos);
        }
    }
}
