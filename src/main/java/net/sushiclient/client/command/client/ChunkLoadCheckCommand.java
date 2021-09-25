package net.sushiclient.client.command.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.task.forge.TaskExecutor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@CommandAlias(value = "checkload", description = "Checks if a chunk is loaded")
public class ChunkLoadCheckCommand {

    @Default
    public void onDefault(Integer x, Integer y, Integer z) {
        Sushi.getProfile().getLogger().send(LogLevel.INFO, "Checking pos (" + x + ", " + y + ", " + z + ")");
        BlockPos pos = new BlockPos(x, y, z);
        AtomicReference<IBlockState> ref = new AtomicReference<>();
        PacketListener packetListener = new PacketListener(pos, ref::set);
        NetHandlerPlayClient co = Minecraft.getMinecraft().getConnection();
        if (co == null) {
            Sushi.getProfile().getLogger().send(LogLevel.ERROR, "Connection is not open");
            return;
        }
        TaskExecutor.newTaskChain()
                .then(() -> {
                    co.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
//                    co.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
                    EventHandlers.register(packetListener);
                })
                .delay(20)
                .then(() -> {
                    EventHandlers.unregister(packetListener);
                    IBlockState result = ref.get();
                    if (result != null) {
                        Sushi.getProfile().getLogger().send(LogLevel.INFO, "Block Data Retrieved: " + result.getBlock().getLocalizedName());
                    } else {
                        Sushi.getProfile().getLogger().send(LogLevel.INFO, "Position (" + x + "," + y + "," + z + ") is not loaded");
                    }
                })
                .execute();
    }

    public static class PacketListener {

        private final BlockPos pos;
        private final Consumer<IBlockState> callback;

        public PacketListener(BlockPos pos, Consumer<IBlockState> callback) {
            this.pos = pos;
            this.callback = callback;
        }

        @EventHandler(timing = EventTiming.PRE)
        public void onPacket(PacketReceiveEvent e) {
            if (!(e.getPacket() instanceof SPacketBlockChange)) return;
            SPacketBlockChange packet = (SPacketBlockChange) e.getPacket();
            if (!packet.getBlockPosition().equals(pos)) return;
            callback.accept(packet.getBlockState());
        }
    }
}
