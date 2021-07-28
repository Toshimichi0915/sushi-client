package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.CollapseMode;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.SmoothCollapseComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleColorPickerComponent;
import net.toshimichi.sushi.gui.theme.simple.SimpleColorPickerHeaderComponent;

import java.awt.Color;

public class SimpleColorComponent extends BasePanelComponent<Component> implements ConfigComponent<Color> {

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
