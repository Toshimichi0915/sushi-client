package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleToggleComponent;
import net.toshimichi.sushi.utils.GuiUtils;

public class SimpleBooleanComponent extends SimpleToggleComponent<Configuration<Boolean>> implements ConfigComponent<Boolean> {

    private final ThemeConstants constants;
    private final Configuration<Boolean> conf;

    public SimpleBooleanComponent(ThemeConstants constants, Configuration<Boolean> conf) {
        super(constants, conf.getValue());
        this.constants = constants;
        this.conf = conf;
        setHeight(14);
    }

    @Override
    public Configuration<Boolean> getValue() {
        return conf;
    }

    @Override
    public void onRender() {
        setToggled(conf.getValue());
        super.onRender();
        GuiUtils.prepareText(conf.getName(), constants.font.getValue(), constants.textColor.getValue(), 9, true).draw(getWindowX() + 1, getWindowY() + 2);
    }

    @Override
    protected void onChange(boolean newValue) {
        conf.setValue(newValue);
    }
}
