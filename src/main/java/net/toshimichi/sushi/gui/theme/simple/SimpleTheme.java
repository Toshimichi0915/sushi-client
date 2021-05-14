package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.ConfigComponentFactory;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.config.*;
import net.toshimichi.sushi.modules.Keybind;
import net.toshimichi.sushi.modules.Module;

import java.util.HashMap;
import java.util.Map;

public class SimpleTheme implements Theme {

    private final ThemeConstants constants;
    private final Configurations configurations;
    private final HashMap<Class<?>, ConfigComponentFactory<?>> factories = new HashMap<>();

    public SimpleTheme(Configurations configurations) {
        this.constants = new ThemeConstants(configurations);
        this.configurations = configurations;
        newFactory(IntRange.class, c -> new SimpleIntRangeComponent(constants, c));
        newFactory(DoubleRange.class, c -> new SimpleDoubleRangeComponent(constants, c));
        newFactory(String.class, c -> new SimpleStringComponent(constants, c));
        newFactory(Keybind.class, c -> new SimpleKeybindComponent(constants, c));
        newFactory(Runnable.class, c -> new SimpleRunnableComponent(constants, c));
        newFactory(Boolean.class, c -> new SimpleBooleanComponent(constants, c));
        newFactory(Integer.class, c -> new SimpleIntComponent(constants, c));
    }

    public <T> void newFactory(Class<T> c, ConfigComponentFactory<T> factory) {
        factories.put(c, factory);
    }

    @Override
    public String getId() {
        return "simple";
    }

    @Override
    public BasePanelComponent<?> newClickGui(Module caller) {
        return new SimpleClickGuiComponent(constants, this, configurations, caller);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ConfigComponent<T> newConfigComponent(Configuration<T> conf) {
        for (Map.Entry<Class<?>, ConfigComponentFactory<?>> entry : factories.entrySet()) {
            if (entry.getKey().equals(conf.getValueClass()))
                return ((ConfigComponentFactory<T>) entry.getValue()).newConfigComponent(conf);
        }
        return null;
    }

}
