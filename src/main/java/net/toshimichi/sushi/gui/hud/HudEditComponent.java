package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.*;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.utils.render.GuiUtils;

import java.awt.Color;
import java.util.ArrayList;

public class HudEditComponent extends BasePanelComponent<CornerComponent> {

    private static final Color ACTIVE_COLOR = new Color(60, 60, 60, 100);
    private static final Color INACTIVE_COLOR = new Color(250, 30, 30, 100);
    private static final Color LINE_COLOR = new Color(100, 160, 60);

    private final Theme theme;
    private final ArrayList<HudElementComponent> inactive = new ArrayList<>();
    private final HudComponent hud;
    private ComponentContext<FrameComponent<?>> configComponent;
    private int holdX;
    private int holdY;
    private int currentX;
    private int currentY;
    private CornerComponent corner;

    public HudEditComponent(Theme theme, HudComponent hud) {
        this.theme = theme;
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
        for (HudElementComponent component : hud) {
            if (component instanceof VirtualHudElementComponent) continue;
            drawBox(component);
            CornerComponent connecting = getConnectingCorner(component);
            CornerComponent connected = getConnectedCorner(component);
            if (connecting != null && connected != null) {
                GuiUtils.drawLine(getCenterWindowX(connecting), getCenterWindowY(connecting),
                        getCenterWindowX(connected), getCenterWindowY(connected), LINE_COLOR, 1);
            }
        }
        for (Component component : this) {
            drawBox(component);
        }
        if (corner != null) {
            GuiUtils.drawLine(holdX, holdY,
                    currentX, currentY, LINE_COLOR, 1);
        }
    }

    private void drawBox(Component component) {
        Color rectColor = ACTIVE_COLOR;
        if (inactive.contains(component)) rectColor = INACTIVE_COLOR;
        GuiUtils.drawRect(component.getWindowX(), component.getWindowY(), component.getWidth(), component.getHeight(), rectColor);
        GuiUtils.drawOutline(component.getWindowX(), component.getWindowY(), component.getWidth(), component.getHeight(), Color.WHITE, 1);
    }

    private double getCenterWindowX(Component component) {
        return (component.getWindowX() + component.getWindowX() + component.getWidth()) / 2;
    }

    private double getCenterWindowY(Component component) {
        return (component.getWindowY() + component.getWindowY() + component.getHeight()) / 2;
    }

    private CornerComponent getConnectingCorner(Component target) {
        CornerComponent connected = getConnectedCorner(target);
        if (connected == null) return null;
        for (CornerComponent component : this) {
            if (component.getParent().equals(target) && target.getOrigin().getOpposite().equals(component.getOrigin()))
                return component;
        }
        return null;
    }

    private CornerComponent getConnectedCorner(Component target) {
        for (CornerComponent component : this) {
            if (component.getParent().equals(target.getParent()) && target.getAnchor().equals(component.getAnchor()))
                return component;
        }
        return null;
    }

    private void calculateWindowComponent(Component target) {
        double oldWindowX = target.getWindowX();
        double oldWindowY = target.getWindowY();
        boolean left = getCenterWindowX(target) < GuiUtils.getWidth() / 2;
        boolean top = getCenterWindowY(target) < GuiUtils.getHeight() / 2;
        Origin origin;
        if (left && top) origin = Origin.TOP_LEFT;
        else if (left) origin = Origin.BOTTOM_LEFT;
        else if (top) origin = Origin.TOP_RIGHT;
        else origin = Origin.BOTTOM_RIGHT;

        target.setOrigin(origin);
        target.setAnchor(origin.toAnchor());
        target.setParent(null);
        target.setWindowX(oldWindowX);
        target.setWindowY(oldWindowY);
    }

    @Override
    public void setFocusedComponent(CornerComponent component) {
        super.setFocusedComponent(component);
        remove(component);
        add(0, component);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        HudElementComponent component = hud.getTopComponent(x, y);
        if (component == null) return;
        if (!(component instanceof BaseHudElementComponent)) return;
        if (type == ClickType.LEFT) {
            double configX = 200;
            double configY = 200;
            if (configComponent != null) {
                configComponent.close();
                configX = configComponent.getOrigin().getX();
                configY = configComponent.getOrigin().getY();
            }
            Component configCategory = theme.newConfigCategoryComponent(((BaseHudElementComponent) component).getConfigurations());
            configComponent = Components.show(theme.newFrameComponent(configCategory), false, false);
            configComponent.getOrigin().setX(configX);
            configComponent.getOrigin().setY(configY);
            configComponent.getOrigin().setWidth(100);
            configComponent.getOrigin().setHeight(200);
        } else {
            if (inactive.contains(component)) inactive.remove(component);
            else inactive.add(component);
        }
    }

    private void onHoldLeft(int fromX, int fromY, int toX, int toY, MouseStatus status) {
        // component
        HudElementComponent from = hud.getTopComponent(fromX, fromY);
        HudElementComponent to = hud.getTopComponent(toX, toY);
        if (this.corner == null && (from != null || to != null)) {
            if (status == MouseStatus.START || from == null) {
                if (from == null) from = to;
                hud.setFocusedComponent(from);
                this.holdX = (int) (toX - from.getWindowX());
                this.holdY = (int) (toY - from.getWindowY());
            }
            from.setWindowX(toX - holdX);
            from.setWindowY(toY - holdY);
            if (from.getParent() == null)
                calculateWindowComponent(from);
        }

        // corner box
        CornerComponent corner = getTopComponent(fromX, fromY);
        if (corner != null && status == MouseStatus.START && !(corner.getParent() instanceof VirtualHudElementComponent)) {
            this.holdX = (int) getCenterWindowX(corner);
            this.holdY = (int) getCenterWindowY(corner);
            this.corner = corner;
        } else if (this.corner != null && status == MouseStatus.END || status == MouseStatus.CANCEL) {
            CornerComponent connected = getTopComponent(toX, toY);
            Component target = this.corner.getParent();
            if (connected == null) {
                calculateWindowComponent(target);
            } else if (!connected.equals(this.corner) && !connected.getParent().equals(this.corner.getParent())) {
                double oldWindowX = target.getWindowX();
                double oldWindowY = target.getWindowY();
                target.setOrigin(this.corner.getOrigin().getOpposite());
                target.setAnchor(connected.getAnchor());
                target.setParent(connected.getParent());
                target.setWindowX(oldWindowX);
                target.setWindowY(oldWindowY);
            }
            this.corner = null;
        }
        if (this.corner != null) {
            currentX = toX;
            currentY = toY;
        }
    }

    private void onHoldRight(int fromX, int fromY, int toX, int toY, MouseStatus status) {
        if (status == MouseStatus.END) onClick(toX, toY, ClickType.RIGHT);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (type == ClickType.LEFT) onHoldLeft(fromX, fromY, toX, toY, status);
        if (type == ClickType.RIGHT) onHoldRight(fromX, fromY, toX, toY, status);
    }

    @Override
    public void onRelocate() {
        if (configComponent != null) {
            FrameComponent<?> comp = configComponent.getOrigin();
            comp.setHeight(comp.getValue().getHeight() + comp.getFrame().getTop() + comp.getFrame().getBottom());
        }
        super.onRelocate();
    }

    @Override
    public void onShow() {
        GuiUtils.lockGame();
        for (HudElementComponent element : hud) {
            addCornerComponents(element);
            if (!element.isActive()) inactive.add(element);
            element.setActive(true);
        }
    }

    @Override
    public void onClose() {
        GuiUtils.unlockGame();
        clear();
        for (HudElementComponent element : hud) {
            element.setActive(!inactive.contains(element));
        }
    }
}
