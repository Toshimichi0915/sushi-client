package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Anchor;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.EmptyFrameComponent;
import net.toshimichi.sushi.gui.Origin;

public class ConfigHudElementComponent<T extends Component> extends EmptyFrameComponent<T> implements HudElementComponent {

    private final String id;
    private final String name;
    private final HudComponent hud;
    private final Configuration<Integer> x;
    private final Configuration<Integer> y;
    private final Configuration<Anchor> anchor;
    private final Configuration<Origin> origin;
    private final Configuration<String> parentId;

    public ConfigHudElementComponent(T component, String id, String name, HudComponent hud, Configurations configurations) {
        super(component);
        this.id = id;
        this.name = name;
        this.hud = hud;
        this.x = configurations.get("element." + id + ".x", name + " X", "X coordinate of " + name, Integer.class, component.getX());
        this.y = configurations.get("element." + id + ".y", name + " Y", "Y coordinate of " + name, Integer.class, component.getY());
        this.anchor = configurations.get("element." + id + ".anchor", name + " Anchor", "Anchor of " + name, Anchor.class, component.getAnchor());
        this.origin = configurations.get("element." + id + ".origin", name + " Origin", "Origin of " + name, Origin.class, component.getOrigin());
        this.parentId = configurations.get("element." + id + "parent", name + " Parent", "Parent of " + name, String.class, null);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getX() {
        return x.getValue();
    }

    @Override
    public int getY() {
        return y.getValue();
    }

    @Override
    public void setX(int x) {
        this.x.setValue(x);
    }

    @Override
    public void setY(int y) {
        this.y.setValue(y);
    }

    @Override
    public Anchor getAnchor() {
        return anchor.getValue();
    }

    @Override
    public void setAnchor(Anchor anchor) {
        this.anchor.setValue(anchor);
    }

    @Override
    public Origin getOrigin() {
        return origin.getValue();
    }

    @Override
    public void setOrigin(Origin origin) {
        this.origin.setValue(origin);
    }

    @Override
    public Component getParent() {
        return hud.getHudComponent(parentId.getValue());
    }

    @Override
    public void setParent(Component component) {
        if (!(component instanceof ConfigHudElementComponent)) return;
        parentId.setValue(((ConfigHudElementComponent<?>) component).getId());
    }
}
