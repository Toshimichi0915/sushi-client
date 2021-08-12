package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

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
