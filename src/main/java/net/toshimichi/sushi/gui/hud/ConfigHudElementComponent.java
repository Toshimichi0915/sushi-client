package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Anchor;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.EmptyFrameComponent;
import net.toshimichi.sushi.gui.Origin;

import java.util.Objects;
import java.util.function.Consumer;

public class ConfigHudElementComponent<T extends HudElementComponent> extends EmptyFrameComponent<T> implements HudElementComponent {

    private final HudComponent hud;
    private final Configuration<Integer> x;
    private final Configuration<Integer> y;
    private final Configuration<Anchor> anchor;
    private final Configuration<Origin> origin;
    private final Configuration<String> parent;

    private final Consumer<Integer> xHandler = this::setX;
    private final Consumer<Integer> yHandler = this::setY;
    private final Consumer<Anchor> anchorHandler = this::setAnchor;
    private final Consumer<Origin> originHandler = this::setOrigin;
    private final Consumer<String> parentHandler = this::setParent;

    public ConfigHudElementComponent(T component, HudComponent hud, Configurations configurations) {
        super(component);
        String id = component.getId();
        String name = component.getName();
        this.hud = hud;
        this.x = configurations.get("element." + id + ".x", name + " X", "X coordinate of " + name, Integer.class, component.getX());
        this.y = configurations.get("element." + id + ".y", name + " Y", "Y coordinate of " + name, Integer.class, component.getY());
        this.anchor = configurations.get("element." + id + ".anchor", name + " Anchor", "Anchor of " + name, Anchor.class, component.getAnchor());
        this.origin = configurations.get("element." + id + ".origin", name + " Origin", "Origin of " + name, Origin.class, component.getOrigin());
        this.parent = configurations.get("element." + id + "parent", name + " Parent", "Parent of " + name, String.class, "");
        setX(x.getValue());
        setY(y.getValue());
        setAnchor(anchor.getValue());
        setOrigin(origin.getValue());
        setParent(parent.getValue());
    }

    private void setParent(String parentId) {
        if (parentId.isEmpty()) {
            setParent((Component) null);
            return;
        }
        HudElementComponent element = hud.getHudElementComponent(parentId);
        if (element == null) return;
        setParent(element);
    }

    @Override
    public String getId() {
        return getValue().getId();
    }

    @Override
    public String getName() {
        return getValue().getName();
    }

    @Override
    public void setX(int x) {
        if (getX() == x) return;
        super.setX(x);
        this.x.setValue(x);
    }

    @Override
    public void setY(int y) {
        if (getY() == y) return;
        super.setY(y);
        this.y.setValue(y);
    }

    @Override
    public void setAnchor(Anchor anchor) {
        if (getAnchor().equals(anchor)) return;
        super.setAnchor(anchor);
        this.anchor.setValue(anchor);
    }

    @Override
    public void setOrigin(Origin origin) {
        if (getOrigin() == origin) return;
        super.setOrigin(origin);
        this.origin.setValue(origin);
    }

    @Override
    public void setParent(Component component) {
        if (Objects.equals(getParent(), component)) return;
        if (component == null) {
            super.setParent(null);
            parent.setValue("");
        } else if (component instanceof HudElementComponent) {
            super.setParent(component);
            parent.setValue(((HudElementComponent) component).getId());
        }
    }

    @Override
    public void onShow() {
        super.onShow();
        x.addHandler(xHandler);
        y.addHandler(yHandler);
        anchor.addHandler(anchorHandler);
        origin.addHandler(originHandler);
        parent.addHandler(parentHandler);
    }

    @Override
    public void onClose() {
        super.onClose();
        x.removeHandler(xHandler);
        y.removeHandler(yHandler);
        anchor.removeHandler(anchorHandler);
        origin.removeHandler(originHandler);
        parent.removeHandler(parentHandler);
    }
}
