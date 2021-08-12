package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleTextComponent;
import net.sushiclient.client.gui.theme.simple.SimpleTextHeaderComponent;

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
