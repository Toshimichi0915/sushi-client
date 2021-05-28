package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleColorPickerComponent;

import java.awt.Color;

public class SimpleColorComponent extends SimpleColorPickerComponent implements ConfigComponent<Color> {

    private final Configuration<Color> configuration;
    private boolean ignoreUpdate;

    public SimpleColorComponent(ThemeConstants constants, Configuration<Color> configuration) {
        super(constants, configuration.getName(), configuration.getValue());
        this.configuration = configuration;
        configuration.addHandler(c -> {
            if (ignoreUpdate) return;
            setColor(c);
        });
    }

    @Override
    protected void onChange(Color color) {
        if (!configuration.getValue().equals(color)) {
            ignoreUpdate = true;
            configuration.setValue(color);
            ignoreUpdate = false;
        }
    }

    @Override
    public Configuration<Color> getValue() {
        return configuration;
    }
}
