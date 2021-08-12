package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleEnumComponent;

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
