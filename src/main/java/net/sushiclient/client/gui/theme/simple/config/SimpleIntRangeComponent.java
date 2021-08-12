package net.sushiclient.client.gui.theme.simple.config;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.gui.theme.simple.SimpleBarComponent;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public class SimpleIntRangeComponent extends SimpleBarComponent implements ConfigComponent<IntRange> {

    private final ThemeConstants constants;
    private final Configuration<IntRange> config;


    public SimpleIntRangeComponent(ThemeConstants constants, Configuration<IntRange> config) {
        super(constants, (double) (config.getValue().getCurrent() - config.getValue().getBottom()) / (config.getValue().getTop() - config.getValue().getBottom()));
        this.config = config;
        this.constants = constants;
    }

    @Override
    public void onRender() {
        super.onRender();
        GuiUtils.prepareText(config.getName(), constants.font.getValue(), constants.textColor.getValue(), 9, false).draw(getWindowX() + 1, getWindowY() + 1);
        TextPreview preview = GuiUtils.prepareText(Integer.toString(config.getValue().getCurrent()), constants.font.getValue(), constants.textColor.getValue(), 9, false);
        preview.draw(getWindowX() + getWidth() - preview.getWidth() - 1, getWindowY() + 1);
    }

    @Override
    protected void onChange(double progress) {
        int rawCurrent = (int) (progress * (config.getValue().getTop() - config.getValue().getBottom()) + config.getValue().getBottom());
        int current = rawCurrent - (rawCurrent % config.getValue().getStep());
        config.setValue(new IntRange(current, config.getValue().getTop(), config.getValue().getBottom(), config.getValue().getStep()));
    }

    @Override
    public Configuration<IntRange> getValue() {
        return config;
    }
}
