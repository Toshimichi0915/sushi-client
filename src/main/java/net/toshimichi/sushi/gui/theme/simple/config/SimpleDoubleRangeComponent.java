package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleBarComponent;
import net.toshimichi.sushi.utils.GuiUtils;
import net.toshimichi.sushi.utils.TextPreview;

public class SimpleDoubleRangeComponent extends SimpleBarComponent implements ConfigComponent<DoubleRange> {

    private final ThemeConstants constants;
    private final Configuration<DoubleRange> config;

    public SimpleDoubleRangeComponent(ThemeConstants constants, Configuration<DoubleRange> config) {
        super(constants, (config.getValue().getCurrent() - config.getValue().getBottom()) / (config.getValue().getTop() - config.getValue().getBottom()));
        this.config = config;
        this.constants = constants;
        setHeight(14);
    }

    @Override
    public void onRender() {
        super.onRender();
        GuiUtils.prepareText(config.getName(), constants.font.getValue(), constants.textColor.getValue(), 9, true).draw(getWindowX() + 1, getWindowY() + 2);
        TextPreview preview = GuiUtils.prepareText(String.format("%." + config.getValue().getDigits() + "f", config.getValue().getCurrent()), constants.font.getValue(), constants.textColor.getValue(), 9, true);
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
