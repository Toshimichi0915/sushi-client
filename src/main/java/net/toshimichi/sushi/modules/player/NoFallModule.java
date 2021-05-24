package net.toshimichi.sushi.modules.player;

import net.minecraft.network.play.client.CPacketPlayer;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.events.player.PlayerPacketEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.PlayerUtils;

public class NoFallModule extends BaseModule {

    private final Configuration<NoFallMode> noFallMode;
    private final Configuration<DoubleRange> distance;
    private final Configuration<Boolean> pauseOnElytra;
    private boolean isElytraFlying;
    private double fallY;

    public NoFallModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        noFallMode = provider.get("mode", "Mode", null, NoFallMode.class, NoFallMode.PACKET);
        distance = provider.get("distance", "Distance", null, DoubleRange.class, new DoubleRange(3, 20, 1, 0.5, 1),
                () -> noFallMode.getValue() == NoFallMode.PACKET, false, 0);
        pauseOnElytra = provider.get("elytra_pause", "Pause On Elytra", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPlayerUpdate(PlayerPacketEvent e) {
        isElytraFlying = getPlayer().isElytraFlying();
    }

    @EventHandler(timing = EventTiming.PRE, priority = 10000)
    public void onPacketSend(PacketSendEvent e) {
        if (!(e.getPacket() instanceof CPacketPlayer)) return;
        CPacketPlayer packet = (CPacketPlayer) e.getPacket();
        double posY = packet.getY(getPlayer().posY);
        if (packet.isOnGround() || posY > fallY) fallY = posY;
        double fallDistance = fallY - posY;

        boolean onGround = packet.isOnGround();
        NoFallMode mode = noFallMode.getValue();
        if (mode == NoFallMode.PACKET) {
            if (fallDistance > distance.getValue().getCurrent() &&
                    (!isElytraFlying || !pauseOnElytra.getValue())) {
                fallY = posY;
                onGround = true;
            }
        } else if (mode == NoFallMode.ON_GROUND) {
            fallY = posY;
            onGround = true;
        } else if (mode == NoFallMode.FLY) {
            onGround = false;
        }
        e.setPacket(PlayerUtils.newCPacketPlayer(packet, 0, 0, 0, 0, 0, onGround, false, false, true));
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "NoFall";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

}
