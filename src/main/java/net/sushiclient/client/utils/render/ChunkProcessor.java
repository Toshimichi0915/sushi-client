package net.sushiclient.client.utils.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.WorldLoadEvent;
import net.sushiclient.client.events.world.ChunkLoadEvent;
import net.sushiclient.client.events.world.ChunkUnloadEvent;
import net.sushiclient.client.mixin.AccessorChunkProviderClient;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.utils.world.WorldEventAdapter;

import java.io.Closeable;
import java.util.ArrayList;

abstract public class ChunkProcessor implements Closeable {

    private final IWorldEventListener listener;
    private final ArrayList<Chunk> queued = new ArrayList<>();
    private boolean closed;
    private World world;

    public ChunkProcessor() {
        this.listener = new ChunkListener();
        EventHandlers.register(this);

        WorldClient world = Minecraft.getMinecraft().world;
        this.world = world;
        if (world == null) return;
        world.addEventListener(listener);
        TaskExecutor.newTaskChain()
                .loop(() -> {
                    if (closed) return new Object();
                    if (!queued.isEmpty()) {
                        queued.removeIf(this::reduce);
                    }
                    return null;
                }).execute();
    }

    public void recalculateAll() {
        reset();
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        AccessorChunkProviderClient provider = (AccessorChunkProviderClient) world.getChunkProvider();
        for (Chunk chunk : provider.getLoadedChunks().values()) {
            if (!chunk.isLoaded()) continue;
            queued.add(chunk);
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onChunkLoad(ChunkLoadEvent e) {
        if (!e.getWorld().equals(world)) return;
        queued.add(e.getChunk());
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (!e.getWorld().equals(world)) return;
        queued.add(e.getChunk());
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldLoad(WorldLoadEvent e) {
        if (world != null) world.removeEventListener(listener);
        world = e.getClient();
        if (world != null) world.addEventListener(listener);
        recalculateAll();
    }

    @Override
    public void close() {
        closed = true;
        EventHandlers.unregister(this);
        if (world != null) world.removeEventListener(listener);
    }

    protected void queueChunk(Chunk chunk) {
        queued.add(chunk);
    }

    public boolean isClosed() {
        return closed;
    }

    public World getWorld() {
        return world;
    }

    abstract protected void reset();

    abstract protected boolean reduce(Chunk chunk);

    abstract protected void onBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags);

    private class ChunkListener extends WorldEventAdapter {
        @Override
        public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            if (!worldIn.equals(world)) return;
            onBlockUpdate(pos, oldState, newState, flags);
        }
    }
}
