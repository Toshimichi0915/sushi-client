package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;
import net.toshimichi.sushi.utils.TextPreview;

public class SimpleDoubleRangeComponent extends SimpleBarComponent<Configuration<DoubleRange>> implements ConfigComponent<DoubleRange> {

    private final ThemeConstants constants;
    private final Configuration<DoubleRange> config;

    private final int fontSize;
    private final boolean shadow;

    public SimpleDoubleRangeComponent(ThemeConstants constants, Configuration<DoubleRange> config, int fontSize, boolean shadow) {
        super(constants, (config.getValue().getCurrent() - config.getValue().getBottom()) / (config.getValue().getTop() - config.getValue().getBottom()));
        this.config = config;
        this.constants = constants;
        this.fontSize = fontSize;
        this.shadow = shadow;
    }

    @Override
    public void onRender() {
        super.onRender();
        GuiUtils.prepareText(config.getName(), constants.font.getValue(), constants.textColor.getValue(), fontSize, shadow).draw(getWindowX() + 1, getWindowY() + 2);
        TextPreview preview = GuiUtils.prepareText(String.format("%." + config.getValue().getDigits() + "f", config.getValue().getCurrent()), constants.font.getValue(), constants.textColor.getValue(), fontSize, shadow);
        preview.draw(getWindowX() + getWidth() - preview.getWidth() - 1, getWindowY() + 2);
    }

    @Override
    protected void onChange(double progress) {
        double rawCurrent = getProgress() * (config.getValue().getTop() - config.getValue().getBottom()) + config.getValue().getBottom();
        double current = rawCurrent - (rawCurrent % config.getValue().getStep());
        config.setValue(new DoubleRange(current, config.getValue().getTop(), config.getValue().getBottom(), config.getValue().getStep(), config.getValue().getDigits()));
    }

    @Override
    public Configuration<DoubleRange> getValue() {
        return config;
    }
}
