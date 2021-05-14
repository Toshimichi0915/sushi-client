package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextComponent;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextHeaderComponent;

public class SimpleIntComponent extends BasePanelComponent<Component> implements ConfigComponent<Integer> {

    private final Configuration<Integer> config;

    public SimpleIntComponent(ThemeConstants constants, Configuration<Integer> config) {
        this.config = config;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleTextHeaderComponent(constants, config.getName()));
        add(new SimpleTextComponent(constants, Integer.toString(config.getValue())) {

            private String oldValue;

            @Override
            protected void onChange(String text) {
                try {
                    if (text.isEmpty() || text.equals("-")) return;
                    config.setValue(Integer.parseInt(text));
                    oldValue = text;
                } catch (NumberFormatException e) {
                    setText(oldValue);
                }
            }
        });
    }

    @Override
    public Configuration<Integer> getValue() {
        return config;
    }
}
