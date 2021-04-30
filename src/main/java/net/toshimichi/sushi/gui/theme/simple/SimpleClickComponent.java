package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class SimpleClickComponent extends BaseComponent {
    private final ThemeConstants constants;
    private final String text;
    private final Runnable onClick;
    private boolean hold;
    private boolean hover;

    public SimpleClickComponent(ThemeConstants constants, String text, Runnable onClick) {
        this.constants = constants;
        this.text = text;
        this.onClick = onClick;
        setHeight(14);
    }

    @Override
    public void onRender() {
        Color color;
        if (hover) color = constants.selectedHoverColor.getValue();
        else if (hold) color = constants.enabledColor.getValue();
        else color = constants.backgroundColor.getValue();
        hover = false;
        hold = false;
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
        GuiUtils.prepareText(text, constants.font.getValue(), constants.textColor.getValue(), 10, false)
                .draw(getWindowX() + 1, getWindowY() + 2);
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        hold = true;
        onClick.run();
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        hold = true;
        if (status == MouseStatus.END)
            onClick.run();
    }
}
