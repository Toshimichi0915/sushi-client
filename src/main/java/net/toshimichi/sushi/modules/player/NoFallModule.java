package net.toshimichi.sushi.modules.player;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerUpdateEvent;
import net.toshimichi.sushi.modules.*;

public class NoFallModule extends BaseModule {

    private final Configuration<NoFallMode> noFallMode;
    private final Configuration<DoubleRange> distance;
    private final Configuration<Boolean> pauseOnElytra;
    private boolean onGround;

    public NoFallModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        noFallMode = provider.get("mode", "Mode", null, NoFallMode.class, NoFallMode.PACKET);
        distance = provider.get("distance", "Distance", null, DoubleRange.class, new DoubleRange(3, 20, 1, 0.5, 1),
                () -> noFallMode.getValue() == NoFallMode.PACKET, null, false, 0);
        pauseOnElytra = provider.get("elytra_pause", "Pause On Elytra", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPrePlayerUpdate(PlayerUpdateEvent e) {
        onGround = getPlayer().onGround;
        NoFallMode mode = noFallMode.getValue();
        if (mode == NoFallMode.PACKET) {
            if (getPlayer().fallDistance > distance.getValue().getCurrent() &&
                    (!getPlayer().isElytraFlying() || !pauseOnElytra.getValue())) {
                getPlayer().onGround = true;
            }
        } else if (mode == NoFallMode.ON_GROUND) {
            getPlayer().onGround = true;
        } else if (mode == NoFallMode.FLY) {
            getPlayer().onGround = false;
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostPlayerUpdate(PlayerUpdateEvent e) {
        getPlayer().onGround = onGround;
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
