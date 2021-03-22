package net.toshimichi.sushi.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.modules.*;

import java.lang.reflect.Field;

public class TimerModule extends BaseModule {

    private static final Field TIMER_FIELD;
    private static final Field TICK_LENGTH_FIELD;

    static {
        TIMER_FIELD = ObfuscationReflectionHelper.findField(Minecraft.class, "timer");
        TICK_LENGTH_FIELD = ObfuscationReflectionHelper.findField(Timer.class, "tickLength");
        TIMER_FIELD.setAccessible(true);
        TICK_LENGTH_FIELD.setAccessible(true);
    }

    private final Configuration<Double> timer;

    public TimerModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        timer = provider.get("multiplier", "Multiplier", "Timer multiplier", Double.class, 1.5);
    }

    private void setTimer(double multiplier) {
        try {
            Timer timer = (Timer) TIMER_FIELD.get(Minecraft.getMinecraft());
            TICK_LENGTH_FIELD.set(timer, (float) (50 / multiplier));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        setTimer(timer.getValue());
    }

    @Override
    public void onDisable() {
        setTimer(1);
    }

    @Override
    public String getDefaultName() {
        return "Timer";
    }

    @Override
    public int getDefaultKeybind() {
        return 0;
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
