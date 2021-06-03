package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

public class SimpleTextHeaderComponent extends BaseComponent {

    private final ThemeConstants constants;
    private final String name;

    public SimpleTextHeaderComponent(ThemeConstants constants, String name) {
        this.constants = constants;
        this.name = name;
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.outlineColor.getValue());
        TextPreview preview = GuiUtils.prepareText(name, constants.font.getValue(), constants.textColor.getValue(), 9, true);
        preview.draw(getWindowX() + (getWidth() - preview.getWidth()) / 2 - 1, getWindowY() + (getHeight() - preview.getHeight()) / 2 - 1);
    }

    @Override
    public void onRelocate() {
        setHeight(10);
        super.onRelocate();
    }
}
