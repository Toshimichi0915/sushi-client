package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.config.data.KeyCode;
import net.toshimichi.sushi.gui.*;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.config.SimpleDoubleRangeComponent;
import net.toshimichi.sushi.gui.theme.simple.config.SimpleIntRangeComponent;
import net.toshimichi.sushi.gui.theme.simple.config.SimpleKeyCodeComponent;
import net.toshimichi.sushi.gui.theme.simple.config.SimpleStringComponent;
import net.toshimichi.sushi.modules.Module;

import java.util.HashMap;
import java.util.Map;

public class SimpleTheme implements Theme {

    private final ThemeConstants constants;
    private final HashMap<Class<?>, ConfigComponentFactory<?>> factories = new HashMap<>();

    public SimpleTheme(Configurations configurations) {
        this.constants = new ThemeConstants(configurations);
        newFactory(IntRange.class, c -> new SimpleIntRangeComponent(constants, c, 9, true));
        newFactory(DoubleRange.class, c -> new SimpleDoubleRangeComponent(constants, c, 9, true));
        newFactory(String.class, c -> new SimpleStringComponent(constants, c));
        newFactory(KeyCode.class, c -> new SimpleKeyCodeComponent(constants, c));
    }

    public <T> void newFactory(Class<T> c, ConfigComponentFactory<T> factory) {
        factories.put(c, factory);
    }

    @Override
    public String getId() {
        return "simple";
    }

    @Override
    public FrameComponent newFrameComponent(Component component) {
        return new SimpleFrameComponent(constants, component);
    }

    @Override
    public PanelComponent<?> newClickGui(Module caller) {
        return new SimpleClickGuiComponent(constants, caller);
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
