package net.toshimichi.sushi.handlers;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.events.input.MousePressEvent;
import net.toshimichi.sushi.events.input.MouseReleaseEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.gui.MouseStatus;
import org.lwjgl.input.Mouse;

public class ComponentMouseHandler {

    private static final int HOLD_DELAY = 10;

    private final ClickStatus[] types = {new ClickStatus(ClickType.LEFT), new ClickStatus(ClickType.RIGHT)};

    private ClickStatus getClickStatus(ClickType type) {
        for (ClickStatus status : types) {
            if (status.type == type)
                return status;
        }
        return null;
    }

    @EventHandler
    public void onMousePress(MousePressEvent e) {
        ClickStatus status = getClickStatus(e.getClickType());
        if (status == null) return;
        status.isLastClicked = status.isClicked;
        status.isClicked = true;
        status.clickMillis = System.currentTimeMillis();
        status.clickX = Mouse.getX();
        status.clickY = Mouse.getY();
    }

    @EventHandler
    public void onMouseRelease(MouseReleaseEvent e) {
        ClickStatus status = getClickStatus(e.getClickType());
        if (status == null) return;
        status.isLastClicked = status.isClicked;
        status.isClicked = false;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        for (ClickType type : ClickType.values()) {

            // fetch/update statuses
            ClickStatus status = getClickStatus(type);
            if (status == null) return;
            status.lastX = status.x;
            status.lastY = status.y;
            status.x = Mouse.getX();
            status.y = Mouse.getY();

            Component lastComponent = Components.getTopComponent(status.lastX, status.lastY);
            Component component = Components.getTopComponent(status.x, status.y);
            if (lastComponent == null) return;

            // component changed
            if (!lastComponent.equals(component) && status.isLastClicked) {
                if (HOLD_DELAY < System.currentTimeMillis() - status.clickMillis) {
                    lastComponent.onHold(status.lastX, status.lastY, status.x, status.y, type, MouseStatus.CANCEL);
                } else {
                    lastComponent.onClick(status.lastX, status.lastY, type);
                }
            }

            // component not changed
            if (component == null) return;
            MouseStatus mouseStatus;
            if (status.isClicked && !status.isLastClicked) mouseStatus = MouseStatus.START;
            else if (!status.isClicked && status.isLastClicked) mouseStatus = MouseStatus.END;
            else mouseStatus = MouseStatus.IN_PROGRESS;

            if (HOLD_DELAY < System.currentTimeMillis() - status.clickMillis) {
                component.onHold(status.lastX, status.lastY, status.x, status.y, type, mouseStatus);
            } else if (mouseStatus == MouseStatus.END) {
                component.onClick(status.x, status.y, type);
            }

            // update click status
            status.isLastClicked = status.isClicked;
        }
    }

    private static class ClickStatus {
        ClickType type;
        boolean isLastClicked;
        boolean isClicked;
        long clickMillis;
        int clickX;
        int clickY;
        int lastX;
        int lastY;
        int x;
        int y;

        public ClickStatus(ClickType type) {
            this.type = type;
        }
    }
}
