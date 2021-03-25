package net.toshimichi.sushi.handlers;

import net.minecraft.util.math.MathHelper;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.events.input.MousePressEvent;
import net.toshimichi.sushi.events.input.MouseReleaseEvent;
import net.toshimichi.sushi.events.tick.RenderTickEvent;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ComponentContext;
import net.toshimichi.sushi.gui.Components;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.utils.GuiUtils;
import org.lwjgl.input.Mouse;

public class ComponentMouseHandler {

    private static final int HOLD_DELAY = 100;
    private static final int CLICK_THRESHOLD = 2;

    private final ClickStatus[] types = {new ClickStatus(ClickType.LEFT), new ClickStatus(ClickType.RIGHT)};

    private ClickStatus getClickStatus(ClickType type) {
        for (ClickStatus status : types) {
            if (status.type == type)
                return status;
        }
        return null;
    }


    @EventHandler(timing = EventTiming.PRE)
    public void onMousePress(MousePressEvent e) {
        ClickStatus status = getClickStatus(e.getClickType());
        if (status == null) return;
        status.isClicked = true;
        status.clickMillis = System.currentTimeMillis();
        status.clickX = GuiUtils.toScaledX(Mouse.getEventX());
        status.clickY = GuiUtils.toScaledY(Mouse.getEventY());
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onMouseRelease(MouseReleaseEvent e) {
        ClickStatus status = getClickStatus(e.getClickType());
        if (status == null) return;
        status.isClicked = false;
    }

    @EventHandler(timing = EventTiming.POST, priority = 500)
    public void onRenderTick(RenderTickEvent e) {
        for (ClickType type : ClickType.values()) {

            // fetch/update statuses
            ClickStatus status = getClickStatus(type);
            if (status == null) continue;
            status.lastX = status.x;
            status.lastY = status.y;
            status.x = GuiUtils.toScaledX(Mouse.getX());
            status.y = GuiUtils.toScaledY(Mouse.getY());

            ComponentContext<?> lastContext = Components.getTopContext(status.lastX, status.lastY);
            ComponentContext<?> context = Components.getTopContext(status.x, status.y);
            Component lastComponent = lastContext == null ? null : lastContext.getOrigin();
            Component component = context == null ? null : context.getOrigin();

            if (!status.isClicked && !status.isLastClicked) {
                if (component != null && component.getOrigin() != null) component.onHover(status.x, status.y);
                continue;
            }
            if (lastComponent == null) continue;

            // component changed
            if (!lastComponent.equals(component) && status.isLastClicked) {
                if (HOLD_DELAY < status.lastTickMillis - status.clickMillis ||
                        CLICK_THRESHOLD < MathHelper.sqrt(Math.pow(status.lastX - status.clickX, 2) + Math.pow(status.lastY - status.clickY, 2))) {
                    lastComponent.onHold(status.lastX, status.lastY, status.x, status.y, type, MouseStatus.IN_PROGRESS);
                    context = Components.getTopContext(status.x, status.y);
                    component = context == null ? null : context.getOrigin();
                    if (!lastComponent.equals(component))
                        lastComponent.onHold(status.x, status.y, status.x, status.y, type, MouseStatus.CANCEL);
                } else {
                    lastComponent.onClick(status.lastX, status.lastY, type);
                }
            }

            // component not changed
            if (component == null) continue;
            MouseStatus mouseStatus;
            if (status.isClicked && !status.isLastClicked) mouseStatus = MouseStatus.START;
            else if (!status.isClicked && status.isLastClicked) mouseStatus = MouseStatus.END;
            else mouseStatus = MouseStatus.IN_PROGRESS;

            if (HOLD_DELAY < status.tickMillis - status.clickMillis ||
                    CLICK_THRESHOLD < MathHelper.sqrt(Math.pow(status.x - status.clickX, 2) + Math.pow(status.y - status.clickY, 2))) {

                if (HOLD_DELAY >= status.lastTickMillis - status.clickMillis &&
                        CLICK_THRESHOLD >= MathHelper.sqrt(Math.pow(status.lastX - status.clickX, 2) + Math.pow(status.lastY - status.clickY, 2))) {
                    component.onHold(status.lastX, status.lastY, status.x, status.y, type, MouseStatus.START);
                } else {
                    component.onHold(status.lastX, status.lastY, status.x, status.y, type, mouseStatus);
                }

            } else if (mouseStatus == MouseStatus.END) {
                component.onClick(status.x, status.y, type);
            }
        }

        for (ClickType type : ClickType.values()) {
            ClickStatus status = getClickStatus(type);
            if (status == null) continue;
            status.isLastClicked = status.isClicked;
            status.lastTickMillis = status.tickMillis;
            status.tickMillis = System.currentTimeMillis();
        }
    }

    private static class ClickStatus {
        ClickType type;
        boolean isClicked;
        boolean isLastClicked;
        long tickMillis;
        long lastTickMillis;
        long clickMillis;
        int clickX;
        int clickY;
        int lastX;
        int lastY;
        int x;
        int y;

        ClickStatus(ClickType type) {
            this.type = type;
        }
    }
}
