package net.sushiclient.client.gui.hud;

import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.Color;

public class CornerComponent extends BaseComponent {

    @Override
    public void onRender() {
        setX(0);
        setY(0);
        setWidth(10);
        setHeight(10);
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), new Color(50, 50, 50, 30));
    }
}
