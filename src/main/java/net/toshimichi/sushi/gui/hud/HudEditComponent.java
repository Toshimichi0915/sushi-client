package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.Origin;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;
import java.util.ArrayList;

public class HudEditComponent extends BasePanelComponent<CornerComponent> {

    private final HudComponent hud;
    private int holdX;
    private int holdY;
    private int currentX;
    private int currentY;
    private CornerComponent corner;
    private int tick;

    public HudEditComponent(HudComponent hud) {
        this.hud = hud;
    }

    private void addCornerComponents(Component parent) {
        ArrayList<CornerComponent> corners = new ArrayList<>();
        for (Origin origin : Origin.values()) {
            CornerComponent component = new CornerComponent();
            component.setOrigin(origin);
            corners.add(component);
        }
        corners.forEach(c -> c.setAnchor(c.getOrigin().toAnchor().getOpposite()));
        corners.forEach(c -> c.setParent(parent));
        corners.forEach(c -> add(c, true));
    }

    @Override
    public void onRender() {
        super.onRender();
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
        for (Component component : hud) {
            if (component instanceof VirtualHudElementComponent) continue;
            drawBox(component);
        }
        for (Component component : this) {
            drawBox(component);
        }
        if (corner != null) {
            GuiUtils.drawLine(holdX, holdY,
                    currentX, currentY, new Color(100, 160, 60), 1);
        }
    }

    private void drawBox(Component component) {
        GuiUtils.drawRect(component.getWindowX(), component.getWindowY(), component.getWidth(), component.getHeight(), new Color(60, 60, 60, 100));
        GuiUtils.drawOutline(component.getWindowX(), component.getWindowY(), component.getWidth(), component.getHeight(), Color.WHITE, 1);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {

        // component
        Component component = hud.getTopComponent(fromX, fromY);
        if (component != null) {
            if (status == MouseStatus.START) {
                this.holdX = fromX - component.getWindowX();
                this.holdY = fromY - component.getWindowY();
            }
            component.setWindowX(toX - holdX);
            component.setWindowY(toY - holdY);
        }

        // corner box
        CornerComponent corner = getTopComponent(fromX, fromY);
        if (status == MouseStatus.START && corner != null && !(corner.getParent() instanceof VirtualHudElementComponent)) {
            this.holdX = fromX;
            this.holdY = fromY;
            this.corner = corner;
        } else if (this.corner != null && status == MouseStatus.END || status == MouseStatus.CANCEL) {
            CornerComponent connected = getTopComponent(toX, toY);
            Component target = this.corner.getParent();
            int oldWindowX = target.getWindowX();
            int oldWindowY = target.getWindowY();
            if (connected == null) {
                target.setParent(null);
            } else if (!connected.equals(this.corner)) {
                target.setAnchor(connected.getAnchor());
                target.setOrigin(connected.getOrigin());
                target.setParent(connected.getParent());
            }
            target.setWindowX(oldWindowX);
            target.setWindowY(oldWindowY);
            this.corner = null;
        }
        if (this.corner != null) {
            currentX = toX;
            currentY = toY;
        }
    }

    @Override
    public void onShow() {
        GuiUtils.lockGame();
        for (HudElementComponent element : hud)
            addCornerComponents(element);
        EventHandlers.register(this);
    }

    @Override
    public void onClose() {
        GuiUtils.unlockGame();
        clear();
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        tick++;
    }
}
