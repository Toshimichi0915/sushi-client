package net.toshimichi.sushi.handlers;

import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.utils.TpsUtils;

public class TpsHandler {

    private long lastTime;

    @EventHandler(timing = EventTiming.PRE, priority = -100)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketTimeUpdate)) return;
        long lastTime = this.lastTime;
        TpsUtils.setTps((System.currentTimeMillis() - lastTime) / 50D);
        this.lastTime = System.currentTimeMillis();
    }
}
