package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.CollapseMode;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleColorPickerComponent;
import net.sushiclient.client.gui.theme.simple.SimpleColorPickerHeaderComponent;

import java.awt.Color;

public class SimpleColorComponent extends AnyPanelComponent implements ConfigComponent<Color> {

    private final SimpleColorPickerComponent simpleColorPickerComponent;
    private final Configuration<Color> configuration;
    private boolean ignoreUpdate;

    public SimpleColorComponent(ThemeConstants constants, Configuration<Color> configuration) {
        simpleColorPickerComponent = new SimpleColorPickerComponent(constants, configuration.getName(), configuration.getValue()) {
            @Override
            protected void onChange(Color color) {
                if (!configuration.getValue().equals(color)) {
                    ignoreUpdate = true;
                    configuration.setValue(color);
                    ignoreUpdate = false;
                }
            }
        };
        SmoothCollapseComponent<?> collapseComponent = new SmoothCollapseComponent<>(simpleColorPickerComponent, CollapseMode.DOWN, 100);
        add(new SimpleColorPickerHeaderComponent(constants, collapseComponent, configuration::getValue, configuration.getName()));
        add(collapseComponent);
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        this.configuration = configuration;
        configuration.addHandler(c -> {
            if (ignoreUpdate) return;
            simpleColorPickerComponent.setColor(c);
        });
    }

    @Override
    public Configuration<Color> getValue() {
        return configuration;
    }
}
