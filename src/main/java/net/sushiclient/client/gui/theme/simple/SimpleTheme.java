package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.ConfigComponentFactory;
import net.sushiclient.client.gui.FrameComponent;
import net.sushiclient.client.gui.base.BasePanelComponent;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.config.*;
import net.sushiclient.client.modules.Keybind;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.utils.render.TextSettings;

import java.awt.Color;
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
        newFactory(Named.class, c -> new SimpleNamedComponent<>(constants, c));
        newFactory(Color.class, c -> new SimpleColorComponent(constants, c));
        newFactory(EspColor.class, c -> new SimpleEspColorComponent(constants, c));
        newFactory(TextSettings.class, c -> new SimpleTextSettingsComponent(constants, c));
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

    @Override
    public Component newConfigCategoryComponent(Configurations configurations) {
        return new SimpleConfigCategoryComponent(this, configurations);
    }

    @Override
    public <T extends Component> FrameComponent<T> newFrameComponent(T component) {
        return new SimpleFrameComponent<>(constants, component);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ConfigComponent<T> newConfigComponent(Configuration<T> conf) {
        for (Map.Entry<Class<?>, ConfigComponentFactory<?>> entry : factories.entrySet()) {
            if (entry.getKey().isAssignableFrom(conf.getValueClass()))
                return ((ConfigComponentFactory<T>) entry.getValue()).newConfigComponent(conf);
        }
        return null;
    }

}
