package net.toshimichi.sushi.modules.player;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.RotateUtils;

public class SpinModule extends BaseModule {

    private final Configuration<IntRange> tickYaw;
    private float yaw;

    public SpinModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        tickYaw = provider.get("tick_yaw", "Yaw", "Yaw per tick", IntRange.class, new IntRange(3, 180, 1, 1));
    }

    @Override
    public void onEnable() {
        RotateUtils.setSync(false);
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        RotateUtils.setSync(true);
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        yaw += (float) tickYaw.getValue().getCurrent();
        RotateUtils.rotate(yaw, 0);
    }

    @Override
    public String getDefaultName() {
        return "Spin";
    }

    @Override
    public int getDefaultKeybind() {
        return 0;
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }

    @Override
    public ConflictType[] getConflictTypes() {
        return new ConflictType[]{ConflictType.ROTATE};
    }
}
