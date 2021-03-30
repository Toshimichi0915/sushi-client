package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class HudEditComponent extends BaseComponent {

    private final HudComponent hud;
    private int holdX;
    private int holdY;

    public HudEditComponent(HudComponent hud) {
        this.hud = hud;
    }

    @Override
    public void onRender() {
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
        for (HudElementComponent component : hud) {
            if (component instanceof VirtualHudElementComponent) continue;
            GuiUtils.drawRect(component.getWindowX(), component.getWindowY(), component.getWidth(), component.getHeight(), new Color(60, 60, 60, 100));
            GuiUtils.drawOutline(component.getWindowX(), component.getWindowY(), component.getWidth(), component.getHeight(), Color.WHITE, 1);
        }
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        HudElementComponent component = hud.getTopComponent(fromX, fromY);
        if (component == null) return;
        if (status == MouseStatus.START) {
            this.holdX = fromX - component.getX();
            this.holdY = fromY - component.getY();
            return;
        }
        component.setX(toX - holdX);
        component.setY(toY - holdY);
    }

    @Override
    public void onShow() {
        GuiUtils.lockGame();
    }

    @Override
    public void onClose() {
        GuiUtils.unlockGame();
    }
}
