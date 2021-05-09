package net.toshimichi.sushi.modules.player;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.TimerUtils;
import net.toshimichi.sushi.utils.TpsUtils;

public class TimerModule extends BaseModule {

    private final Configuration<DoubleRange> multiplier;
    private final Configuration<Boolean> tpsSync;

    public TimerModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        multiplier = provider.get("multiplier", "Mutliplier", null, DoubleRange.class, new DoubleRange(1, 10, 0.1, 0.1, 1));
        tpsSync = provider.get("tps_sync", "TPS Sync", null, Boolean.class, false);
        multiplier.addHandler(d -> {
            if (!isEnabled()) return;
            TimerUtils.pop();
            push();
        });
    }

    private void push() {
        TimerUtils.push((float) (multiplier.getValue().getCurrent() * TpsUtils.getTps() / 20));
    }

    @Override
    public void onEnable() {
        push();
    }

    @Override
    public void onDisable() {
        TimerUtils.pop();
    }

    @Override
    public String getDefaultName() {
        return "Timer";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
