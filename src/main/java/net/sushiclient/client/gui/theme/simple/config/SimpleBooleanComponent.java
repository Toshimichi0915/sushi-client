package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleToggleComponent;
import net.sushiclient.client.utils.render.GuiUtils;

public class SimpleBooleanComponent extends SimpleToggleComponent<Configuration<Boolean>> implements ConfigComponent<Boolean> {

    private final ThemeConstants constants;
    private final Configuration<Boolean> conf;

    public SimpleBooleanComponent(ThemeConstants constants, Configuration<Boolean> conf) {
        super(constants, conf.getValue());
        this.constants = constants;
        this.conf = conf;
        setHeight(12);
    }

    @Override
    public Configuration<Boolean> getValue() {
        return conf;
    }

    @Override
    public void onRender() {
        setToggled(conf.getValue());
        super.onRender();
        GuiUtils.prepareText(conf.getName(), constants.font.getValue(), constants.textColor.getValue(), 9, false).draw(getWindowX() + 1, getWindowY() + 1);
    }

    @Override
    protected void onChange(boolean newValue) {
        conf.setValue(newValue);
    }
}
