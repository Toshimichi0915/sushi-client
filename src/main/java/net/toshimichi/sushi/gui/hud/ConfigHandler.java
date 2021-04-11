package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Anchor;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ComponentHandler;
import net.toshimichi.sushi.gui.Origin;

import java.util.Objects;
import java.util.function.Consumer;

public class ConfigHandler implements ComponentHandler {

    private final Component component;
    private final HudComponent hud;
    private final Configuration<Integer> x;
    private final Configuration<Integer> y;
    private final Configuration<Anchor> anchor;
    private final Configuration<Origin> origin;
    private final Configuration<String> parent;

    private final Consumer<Integer> xHandler;
    private final Consumer<Integer> yHandler;
    private final Consumer<Anchor> anchorHandler;
    private final Consumer<Origin> originHandler;
    private final Consumer<String> parentHandler;

    public ConfigHandler(HudElementComponent component, HudComponent hud, Configurations configurations) {
        String id = component.getId();
        String name = component.getName();
        this.component = component;
        this.hud = hud;
        this.x = configurations.temp("element." + id + ".x", name + " X", "X coordinate of " + name, Integer.class, component.getX());
        this.y = configurations.temp("element." + id + ".y", name + " Y", "Y coordinate of " + name, Integer.class, component.getY());
        this.anchor = configurations.temp("element." + id + ".anchor", name + " Anchor", "Anchor of " + name, Anchor.class, component.getAnchor());
        this.origin = configurations.temp("element." + id + ".origin", name + " Origin", "Origin of " + name, Origin.class, component.getOrigin());
        this.parent = configurations.temp("element." + id + ".parent", name + " Parent", "Parent of " + name, String.class, "");
        xHandler = component::setX;
        yHandler = component::setY;
        anchorHandler = component::setAnchor;
        originHandler = component::setOrigin;
        parentHandler = this::setParent;
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
    public void setX(int x) {
        if (this.x.getValue() == x) return;
        this.x.setValue(x);
    }

    @Override
    public void setY(int y) {
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
    public void onShow() {
        component.setX(x.getValue());
        component.setY(y.getValue());
        component.setAnchor(anchor.getValue());
        component.setOrigin(origin.getValue());
        setParent(parent.getValue());

        x.addHandler(xHandler);
        y.addHandler(yHandler);
        anchor.addHandler(anchorHandler);
        origin.addHandler(originHandler);
        parent.addHandler(parentHandler);
    }

    @Override
    public void onClose() {
        x.removeHandler(xHandler);
        y.removeHandler(yHandler);
        anchor.removeHandler(anchorHandler);
        origin.removeHandler(originHandler);
        parent.removeHandler(parentHandler);
    }
}
