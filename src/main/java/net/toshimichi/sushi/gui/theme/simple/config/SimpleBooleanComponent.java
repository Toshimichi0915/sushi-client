package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleToggleComponent;

public class SimpleBooleanComponent extends SimpleToggleComponent<Configuration<Boolean>> implements ConfigComponent<Boolean> {

    private final Configuration<Boolean> conf;

    public SimpleBooleanComponent(ThemeConstants constants, Configuration<Boolean> conf) {
        super(constants, conf.getValue());
        this.conf = conf;
    }

    @Override
    public Configuration<Boolean> getValue() {
        return conf;
    }

    @Override
    public void onRender() {
        setToggled(conf.getValue());
        super.onRender();
    }

    @Override
    protected void onChange(boolean newValue) {
        conf.setValue(newValue);
    }
}
