package net.toshimichi.sushi.modules.movement;

import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.config.data.Named;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.player.PlayerTravelEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.render.hole.HoleInfo;
import net.toshimichi.sushi.utils.render.hole.HoleType;
import net.toshimichi.sushi.utils.render.hole.HoleUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class AnchorModule extends BaseModule {

    @Config(id = "instant", name = "Instant")
    public Boolean instant = true;

    @Config(id = "range", name = "Range")
    public IntRange range = new IntRange(3, 10, 1, 1);

    @Config(id = "pitch_trigger", name = "Pitch Trigger")
    public Boolean pitchTrigger = true;

    @Config(id = "pitch", name = "Pitch", when = "pitch_trigger")
    public IntRange pitch = new IntRange(80, 90, 0, 1);

    @Config(id = "pause_in_hole", name = "Pause In Hole")
    public Boolean pauseInHole = true;

    @Config(id = "pause_ticks", name = "Pause Ticks", when = "pause_in_hole")
    public IntRange pauseTicks = new IntRange(0, 100, 0, 1);

    @Config(id = "disable_in_hole", name = "Disable In Hole")
    public Boolean disableInHole = false;

    @Config(id = "hole_selector", name = "Allowed Hole")
    public HoleSelector selector = HoleSelector.BOTH;

    private boolean isPausing;

    public AnchorModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
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
    public void onClientTick(PlayerTravelEvent e) {
        if (isPausing) return;
        boolean isInHole = getHole(0, selector.getAllowedTypes()) != null;
        boolean validPitch = !pitchTrigger || getPlayer().rotationPitch > pitch.getCurrent();
        if (isInHole) {
            if (disableInHole) {
                setEnabled(false);
                return;
            }
            if (pauseInHole) {
                TaskExecutor.newTaskChain()
                        .delay(pauseTicks.getCurrent())
                        .then(() -> isPausing = false)
                        .execute();
                isPausing = true;
            }
        }

        if (!validPitch) return;
        HoleInfo hole = getHole(range.getCurrent(), selector.getAllowedTypes());
        if (hole == null) return;
        double posY = instant ? hole.getBlockPos()[0].getY() : getPlayer().posY;
        getPlayer().motionX = 0;
        if (instant) getPlayer().motionY = 0;
        getPlayer().motionZ = 0;
        PositionUtils.move((int) getPlayer().posX + 0.5, posY, (int) getPlayer().posZ + 0.5,
                0, 0, true, false, DesyncMode.NONE);
    }

    private HoleInfo getHole(int range, HoleType... allowed) {
        BlockPos from = BlockUtils.toBlockPos(getPlayer().getPositionVector().subtract(0, range, 0));
        BlockPos to = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        AtomicReference<HoleInfo> result = new AtomicReference<>();
        HoleUtils.findHoles(getWorld(), from, to, info -> {
            if (Arrays.asList(allowed).contains(info.getHoleType())) {
                result.set(info);
            }
        });
        return result.get();
    }

    @Override
    public String getDefaultName() {
        return "Anchor";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }

    public enum HoleSelector implements Named {
        BOTH("Both", HoleType.SAFE, HoleType.UNSAFE), BEDROCK("Bedrock", HoleType.SAFE);

        private final String name;
        private final HoleType[] allowed;

        HoleSelector(String name, HoleType... allowed) {
            this.name = name;
            this.allowed = allowed;
        }

        @Override
        public String getName() {
            return name;
        }

        public HoleType[] getAllowedTypes() {
            return allowed;
        }
    }
}
