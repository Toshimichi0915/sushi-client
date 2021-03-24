package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class ClickComponent extends BaseComponent {
    private final ThemeConstants constants;
    private final String text;
    private final Runnable onClick;
    private boolean clicked;
    private boolean hover;

    public ClickComponent(ThemeConstants constants, String text, Runnable onClick) {
        this.constants = constants;
        this.text = text;
        this.onClick = onClick;
        setHeight(14);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.disabledColor.getValue());
        Color textColor;
        if (clicked) textColor = constants.enabledColor.getValue();
        else if (hover) textColor = constants.selectedHoverColor.getValue();
        else textColor = constants.textColor.getValue();
        GuiUtils.prepareText(text, constants.font.getValue(), textColor, 10, false)
                .draw(getWindowX() + 1, getWindowY() + 2);

        hover = false;
        clicked = false;
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        clicked = true;
        onClick.run();
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        clicked = true;
        if (status == MouseStatus.END)
            onClick.run();
    }
}
