package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class SimpleRunnableComponent extends BaseConfigComponent<Runnable> {

    private final ThemeConstants constants;
    private final Configuration<Runnable> configuration;
    private boolean hover;
    private boolean hold;

    public SimpleRunnableComponent(ThemeConstants constants, Configuration<Runnable> configuration) {
        super(configuration);
        this.constants = constants;
        this.configuration = configuration;
    }

    @Override
    public void onRender() {
        Color color;
        if (hover) color = constants.selectedHoverColor.getValue();
        else if (hold) color = constants.enabledColor.getValue();
        else color = constants.outlineColor.getValue();

        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
        GuiUtils.prepareText(configuration.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, true);
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        hold = true;
    }
}
