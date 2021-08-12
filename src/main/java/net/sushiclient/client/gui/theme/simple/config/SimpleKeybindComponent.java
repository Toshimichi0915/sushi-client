package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleEnumComponent;
import net.sushiclient.client.modules.ActivationType;
import net.sushiclient.client.modules.Keybind;

public class SimpleKeybindComponent extends AnyPanelComponent implements ConfigComponent<Keybind> {

    private final Configuration<Keybind> conf;
    private ActivationType type;
    private int[] keys;

    public SimpleKeybindComponent(ThemeConstants constants, Configuration<Keybind> conf) {
        this.conf = conf;
        this.type = conf.getValue().getActivationType();
        this.keys = conf.getValue().getKeys();
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new SimpleEnumComponent<ActivationType>(constants, "Activation", conf.getValue().getActivationType(), ActivationType.class) {

            @Override
            protected void onChange(ActivationType newValue) {
                type = newValue;
                conf.setValue(new Keybind(type, keys));
            }
        });
        add(new SimpleKeyComponent(constants, conf.getValue().getKeys()) {
            @Override
            protected void onChange(int[] newValue) {
                keys = newValue;
                conf.setValue(new Keybind(type, keys));
            }
        });
    }

    @Override
    public Configuration<Keybind> getValue() {
        return conf;
    }
}
