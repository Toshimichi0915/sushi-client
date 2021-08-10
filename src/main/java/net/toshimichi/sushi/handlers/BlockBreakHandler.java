package net.toshimichi.sushi.handlers;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.utils.TickUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

public class BlockBreakHandler {
    @EventHandler(timing = EventTiming.PRE, priority = 100000)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayerDigging)) return;
        CPacketPlayerDigging packet = (CPacketPlayerDigging) e.getPacket();
        if (packet.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            BlockUtils.setBreakingBlock(packet.getPosition(), TickUtils.current());
        } else if (packet.getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
            BlockUtils.setBreakingBlock(null, TickUtils.current());
        }
    }
}
