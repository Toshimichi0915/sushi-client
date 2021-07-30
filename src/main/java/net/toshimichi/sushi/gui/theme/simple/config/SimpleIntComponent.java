package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.AnyPanelComponent;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextComponent;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextHeaderComponent;

public class SimpleIntComponent extends AnyPanelComponent implements ConfigComponent<Integer> {

    private final SimpleTextComponent textComponent;
    private final Configuration<Integer> config;

    public SimpleIntComponent(ThemeConstants constants, Configuration<Integer> config) {
        this.config = config;
        textComponent = new SimpleTextComponent(constants, Integer.toString(config.getValue()), true) {

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
        };
        add(new SimpleTextHeaderComponent(constants, config.getName()));
        add(textComponent);
        config.addHandler(i -> textComponent.setText(Integer.toString(config.getValue())));
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
    }

    @Override
    public Configuration<Integer> getValue() {
        return config;
    }
}
