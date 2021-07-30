package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.AnyPanelComponent;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextComponent;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextHeaderComponent;

public class SimpleStringComponent extends AnyPanelComponent implements ConfigComponent<String> {

    private final Configuration<String> config;

    public SimpleStringComponent(ThemeConstants constants, Configuration<String> config) {
        this.config = config;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleTextHeaderComponent(constants, config.getName()));
        add(new SimpleTextComponent(constants, config.getValue(), !config.isTemporary()) {
            @Override
            protected void onChange(String text) {
                config.setValue(text);
            }
        });
    }

    @Override
    public Configuration<String> getValue() {
        return config;
    }
}
