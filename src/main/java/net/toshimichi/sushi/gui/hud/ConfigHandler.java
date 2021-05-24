package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Anchor;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Origin;

import java.util.Objects;
import java.util.function.Consumer;

public class ConfigHandler implements HudElementComponentHandler {

    private final HudElementComponent component;
    private final HudComponent hud;
    private final Configuration<Double> x;
    private final Configuration<Double> y;
    private final Configuration<Anchor> anchor;
    private final Configuration<Origin> origin;
    private final Configuration<String> parent;
    private final Configuration<Boolean> active;

    private final Consumer<Double> xHandler;
    private final Consumer<Double> yHandler;
    private final Consumer<Anchor> anchorHandler;
    private final Consumer<Origin> originHandler;
    private final Consumer<String> parentHandler;
    private final Consumer<Boolean> activeHandler;

    public ConfigHandler(HudElementComponent component, HudComponent hud, Configurations configurations) {
        String id = component.getId();
        String name = component.getName();
        this.component = component;
        this.hud = hud;
        this.x = configurations.get("element." + id + ".x", name + " X", "X coordinate of " + name, Double.class, component.getX(), () -> false, false, 0);
        this.y = configurations.get("element." + id + ".y", name + " Y", "Y coordinate of " + name, Double.class, component.getY(), () -> false, false, 0);
        this.anchor = configurations.get("element." + id + ".anchor", name + " Anchor", "Anchor of " + name, Anchor.class, component.getAnchor(), () -> false, false, 0);
        this.origin = configurations.get("element." + id + ".origin", name + " Origin", "Origin of " + name, Origin.class, component.getOrigin(), () -> false, false, 0);
        this.parent = configurations.get("element." + id + ".parent", name + " Parent", "Parent of " + name, String.class, "", () -> false, false, 0);
        this.active = configurations.get("element." + id + ".activity", name + " Activity", "Activity of " + name, Boolean.class, component.isActive(), () -> false, false, 0);
        xHandler = component::setX;
        yHandler = component::setY;
        anchorHandler = component::setAnchor;
        originHandler = component::setOrigin;
        parentHandler = this::setParent;
        activeHandler = this::setActive;
    }

    private void setParent(String parentId) {
        if (parentId.isEmpty()) {
            component.setParent(null);
        } else {
            HudElementComponent element = hud.getHudElementComponent(parentId);
            if (element == null) return;
            component.setParent(element);
        }
    }


    @Override
    public void setX(double x) {
        if (this.x.getValue() == x) return;
        this.x.setValue(x);
    }

    @Override
    public void setY(double y) {
        if (this.y.getValue() == y) return;
        this.y.setValue(y);
    }

    @Override
    public void setAnchor(Anchor anchor) {
        if (this.anchor.getValue().equals(anchor)) return;
        this.anchor.setValue(anchor);
    }

    @Override
    public void setOrigin(Origin origin) {
        if (this.origin.getValue() == origin) return;
        this.origin.setValue(origin);
    }

    @Override
    public void setParent(Component parent) {
        if (Objects.equals(hud.getHudElementComponent(this.parent.getValue()), parent)) return;
        if (parent == null) {
            this.parent.setValue("");
        } else if (parent instanceof HudElementComponent) {
            this.parent.setValue(((HudElementComponent) parent).getId());
        }
    }

    @Override
    public void setActive(boolean active) {
        if (this.active.getValue().equals(active)) return;
        this.active.setValue(active);
    }

    @Override
    public void onShow() {
        component.setX(x.getValue());
        component.setY(y.getValue());
        component.setAnchor(anchor.getValue());
        component.setOrigin(origin.getValue());
        component.setActive(active.getValue());
        setParent(parent.getValue());

        x.addHandler(xHandler);
        y.addHandler(yHandler);
        anchor.addHandler(anchorHandler);
        origin.addHandler(originHandler);
        parent.addHandler(parentHandler);
        active.addHandler(activeHandler);
    }

    @Override
    public void onClose() {
        x.removeHandler(xHandler);
        y.removeHandler(yHandler);
        anchor.removeHandler(anchorHandler);
        origin.removeHandler(originHandler);
        parent.removeHandler(parentHandler);
        active.removeHandler(activeHandler);
    }
}
