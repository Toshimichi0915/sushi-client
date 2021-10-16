package net.sushiclient.client.handlers;

import net.minecraft.util.math.MathHelper;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.events.input.MousePressEvent;
import net.sushiclient.client.events.input.MouseReleaseEvent;
import net.sushiclient.client.events.tick.GameTickEvent;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ComponentContext;
import net.sushiclient.client.gui.Components;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.utils.render.GuiUtils;
import org.lwjgl.input.Mouse;

import java.util.Objects;

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

    public static ComponentContext<?> getTopContext(int x, int y) {
        for (ComponentContext<?> context : Components.getAll()) {
            if (context.isOverlay()) continue;
            Component component = context.getOrigin();
            double minX = component.getWindowX();
            double minY = component.getWindowY();
            double maxX = minX + component.getWidth();
            double maxY = minY + component.getHeight();
            if (minX <= x && x <= maxX && minY <= y && y <= maxY)
                return context;
        }
        return null;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onMousePress(MousePressEvent e) {
        if (!GuiUtils.isGameLocked()) return;
        ClickStatus status = getClickStatus(e.getClickType());
        if (status == null) return;
        status.isClicked = true;
        status.clickMillis = System.currentTimeMillis();
        status.clickX = (int) GuiUtils.toScaledX(Mouse.getEventX());
        status.clickY = (int) GuiUtils.toScaledY(Mouse.getEventY());
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onMouseRelease(MouseReleaseEvent e) {
        if (!GuiUtils.isGameLocked()) return;
        ClickStatus status = getClickStatus(e.getClickType());
        if (status == null) return;
        status.isClicked = false;
    }

    @EventHandler(timing = EventTiming.POST, priority = -100)
    public void onGameTick(GameTickEvent e) {
        if (!GuiUtils.isGameLocked()) return;

        for (ClickType type : ClickType.values()) {

            // fetch/update statuses
            ClickStatus status = getClickStatus(type);
            if (status == null) continue;
            status.lastX = status.x;
            status.lastY = status.y;
            status.x = (int) GuiUtils.toScaledX(Mouse.getX());
            status.y = (int) GuiUtils.toScaledY(Mouse.getY());

            ComponentContext<?> lastContext = getTopContext(status.lastX, status.lastY);
            ComponentContext<?> context = getTopContext(status.x, status.y);
            Component lastComponent = lastContext == null ? null : lastContext.getOrigin();
            Component component = context == null ? null : context.getOrigin();

            if (!status.isClicked && !status.isLastClicked) {
                if (component != null) component.onHover(status.x, status.y);
                continue;
            }

            // component changed
            if (!Objects.equals(component, lastComponent) && status.isLastClicked &&
                    (HOLD_DELAY < status.lastTickMillis - status.clickMillis ||
                            CLICK_THRESHOLD < MathHelper.sqrt(Math.pow(status.lastX - status.clickX, 2) + Math.pow(status.lastY - status.clickY, 2)))) {
                if (lastComponent != null) {
                    lastComponent.onHold(status.lastX, status.lastY, status.x, status.y, type, MouseStatus.IN_PROGRESS);
                    context = getTopContext(status.x, status.y);
                    component = context == null ? null : context.getOrigin();
                    if (!lastComponent.equals(component))
                        lastComponent.onHold(status.x, status.y, status.x, status.y, type, MouseStatus.CANCEL);
                }
                if (component != null && !component.equals(lastComponent)) {
                    component.onHold(status.lastX, status.lastY, status.x, status.y, type, MouseStatus.START);
                }
                return;
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
                    if (mouseStatus != MouseStatus.END)
                        component.onHold(status.lastX, status.lastY, status.x, status.y, type, MouseStatus.START);
                    else
                        component.onClick(status.x, status.y, type);
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

        if (Mouse.hasWheel() && Components.getTopContext() != null) {
            Components.getTopContext().getOrigin().onScroll(0, Mouse.getDWheel());
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
