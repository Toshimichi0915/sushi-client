package net.toshimichi.sushi.modules.player;

import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.gui.hud.TextElementComponent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;

public class LockYawModule extends BaseModule {

    private final Configuration<Integer> x;
    private final Configuration<Integer> z;
    private final Configuration<Runnable> toOverworld;
    private final Configuration<Runnable> toNether;

    public LockYawModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        x = provider.get("x", "X", null, Integer.class, 0);
        z = provider.get("z", "Z", null, Integer.class, 0);
        toOverworld = provider.temp("to_overworld", "To Overworld", null, Runnable.class, () -> {
            x.setValue(x.getValue() * 8);
            z.setValue(z.getValue() * 8);
        });
        toNether = provider.temp("to_nether", "To Nether", null, Runnable.class, () -> {
            x.setValue(x.getValue() / 8);
            z.setValue(z.getValue() / 8);
        });
        addElementFactory(TargetComponent::new, id + ".target", "Target Coordinates");
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        float pitch = getPlayer().rotationPitch;
        PositionUtils.lookAt(new Vec3d(x.getValue(), 0, z.getValue()), DesyncMode.NONE);
        getPlayer().rotationPitch = pitch;
    }

    @Override
    public String getDefaultName() {
        return "LockYaw";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    private class TargetComponent extends TextElementComponent {

        private final Configuration<String> format;

        public TargetComponent(Configurations configurations, String id, String name) {
            super(configurations, id, name);
            format = getConfiguration("format", "Format", null, String.class, "Target: {x} {z}");
        }

        @Override
        protected String getText() {
            return format.getValue()
                    .replace("{x}", Integer.toString(x.getValue()))
                    .replace("{z}", Integer.toString(z.getValue()));
        }
    }
}
