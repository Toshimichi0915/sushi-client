package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleBarComponent;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

public class SimpleIntRangeComponent extends SimpleBarComponent implements ConfigComponent<IntRange> {

    private final ThemeConstants constants;
    private final Configuration<IntRange> config;


    public SimpleIntRangeComponent(ThemeConstants constants, Configuration<IntRange> config) {
        super(constants, (double) (config.getValue().getCurrent() - config.getValue().getBottom()) / (config.getValue().getTop() - config.getValue().getBottom()));
        this.config = config;
        this.constants = constants;
        setHeight(14);
    }

    @Override
    public void onRender() {
        super.onRender();
        GuiUtils.prepareText(config.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, false).draw(getWindowX() + 1, getWindowY() + 2);
        TextPreview preview = GuiUtils.prepareText(Integer.toString(config.getValue().getCurrent()), constants.font.getValue(), constants.textColor.getValue(), 10, false);
        preview.draw(getWindowX() + getWidth() - preview.getWidth() - 1, getWindowY() + 2);
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
