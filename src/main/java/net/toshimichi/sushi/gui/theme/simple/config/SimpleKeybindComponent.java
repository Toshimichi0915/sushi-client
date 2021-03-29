package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleEnumComponent;
import net.toshimichi.sushi.modules.ActivationType;
import net.toshimichi.sushi.modules.Keybind;

public class SimpleKeybindComponent extends BasePanelComponent<Component> implements ConfigComponent<Keybind> {

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
