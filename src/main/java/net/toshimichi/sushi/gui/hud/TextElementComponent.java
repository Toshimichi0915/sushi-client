package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

abstract public class TextElementComponent extends BaseHudElementComponent {
    public TextElementComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public void onRender() {
        TextPreview preview = GuiUtils.prepareText(getText(), getTextSettings("text").getValue());
        preview.draw(getWindowX() + 1, getWindowY() + 1);
        setWidth(preview.getWidth() + 3);
        setHeight(preview.getHeight() + 4);
    }

    abstract protected String getText();
}
