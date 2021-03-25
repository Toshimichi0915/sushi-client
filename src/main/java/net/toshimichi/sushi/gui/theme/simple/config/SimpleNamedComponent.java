package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.data.Named;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleEnumComponent;

public class SimpleNamedComponent<T extends Named> extends SimpleEnumComponent<T> implements ConfigComponent<T> {

    private final Configuration<T> conf;

    public SimpleNamedComponent(ThemeConstants constants, Configuration<T> conf) {
        super(constants, conf.getName(), conf.getValue(), conf.getValueClass());
        this.conf = conf;
    }


    @Override
    public Configuration<T> getValue() {
        return conf;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onChange(Named newValue) {
        conf.setValue((T) newValue);
    }
}
