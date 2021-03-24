package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextComponent;

public class SimpleStringComponent extends SimpleTextComponent implements ConfigComponent<String> {

    private final Configuration<String> config;

    public SimpleStringComponent(ThemeConstants constants, Configuration<String> config, int fontSize, boolean shadow) {
        super(constants, config.getValue(), fontSize, shadow);
        this.config = config;
    }

    @Override
    public Configuration<String> getValue() {
        return config;
    }

    @Override
    protected void onChange(String text) {
        config.setValue(text);
    }
}
