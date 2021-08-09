package net.toshimichi.sushi.command.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.task.forge.TaskExecutor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@CommandAlias("checkload")
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
                        Sushi.getProfile().getLogger().send(LogLevel.INFO, "Block Data Retrived: " + result.getBlock().getLocalizedName());
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
