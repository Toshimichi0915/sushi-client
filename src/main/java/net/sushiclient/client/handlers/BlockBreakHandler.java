package net.sushiclient.client.handlers;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.utils.TickUtils;
import net.sushiclient.client.utils.world.BlockUtils;

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
