package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleTextComponent;
import net.sushiclient.client.gui.theme.simple.SimpleTextHeaderComponent;

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
