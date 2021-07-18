package net.toshimichi.sushi.modules.player;

import net.minecraft.util.math.MathHelper;
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
import net.toshimichi.sushi.utils.player.SpeedUtils;

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
        addElementFactory(EtaComponent::new, id + ".eta", "Estimated Time Arrival");
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
            format = getConfiguration("target", "Target", null, String.class, "Target: {x} {z}");
        }

        @Override
        protected String getText() {
            return format.getValue()
                    .replace("{x}", Integer.toString(x.getValue()))
                    .replace("{z}", Integer.toString(z.getValue()));
        }
    }

    private class EtaComponent extends TextElementComponent {
        private final Configuration<String> format;

        public EtaComponent(Configurations configurations, String id, String name) {
            super(configurations, id, name);
            this.format = configurations.get("eta", "ETA", null, String.class, "{h} hours {m} min {s} sec (total: {tm} min");
        }

        @Override
        protected String getText() {
            float vec = MathHelper.sqrt(Math.pow(x.getValue() - getPlayer().posX, 2) + Math.pow(z.getValue() - getPlayer().posZ, 2));
            double mps = SpeedUtils.getMps(getPlayer());
            double seconds = mps == 0 ? 0 : vec / mps;
            String th, tm, ts, h, m, s;
            if (mps < 0.1) {
                th = tm = ts = h = m = s = "Inf";
            } else {
                th = Integer.toString((int) seconds / 60 / 60);
                tm = Integer.toString((int) seconds / 60);
                ts = Integer.toString((int) seconds);
                h = Integer.toString((int) seconds / 60 / 60 % 60);
                m = Integer.toString((int) seconds / 60 % 60);
                s = Integer.toString((int) seconds % 60);
            }
            return format.getValue()
                    .replace("{th}", th)
                    .replace("{tm}", tm)
                    .replace("{ts}", ts)
                    .replace("{h}", h)
                    .replace("{m}", m)
                    .replace("{s}", s);
        }
    }
}
