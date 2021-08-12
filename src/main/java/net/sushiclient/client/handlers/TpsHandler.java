package net.sushiclient.client.handlers;

import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketReceiveEvent;
import net.sushiclient.client.utils.TpsUtils;

public class TpsHandler {

    private long lastTime;

    @EventHandler(timing = EventTiming.PRE, priority = -100)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketTimeUpdate)) return;
        long lastTime = this.lastTime;
        TpsUtils.setTps(20000D / (System.currentTimeMillis() - lastTime));
        this.lastTime = System.currentTimeMillis();
    }
}
