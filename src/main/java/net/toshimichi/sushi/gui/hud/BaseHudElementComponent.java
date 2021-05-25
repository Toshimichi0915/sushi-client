package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.ComponentHandler;
import net.toshimichi.sushi.gui.base.BaseComponent;

abstract public class BaseHudElementComponent extends BaseComponent implements HudElementComponent {

    private final Configurations configurations;
    private final String id;
    private final String name;
    private boolean active = true;

    public BaseHudElementComponent(Configurations configurations, String id, String name) {
        this.configurations = configurations;
        this.id = id;
        this.name = name;
    }

    public Configurations getConfigurations() {
        return configurations;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        for (ComponentHandler handler : getHandlers()) {
            if (handler instanceof HudElementComponentHandler) {
                ((HudElementComponentHandler) handler).setActive(active);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
