package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.EspColor;
import net.toshimichi.sushi.gui.ComponentHandler;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.utils.render.TextSettings;

import java.awt.Color;
import java.util.HashSet;

abstract public class BaseHudElementComponent extends BaseComponent implements HudElementComponent {

    private static final TextSettings DEFAULT_TEXT_SETTINGS
            = new TextSettings("", new EspColor(Color.WHITE, true), 9, true);
    private final HashSet<Configuration<TextSettings>> textSettings = new HashSet<>();
    private final Configurations configurations;
    private final String id;
    private final String name;
    private boolean active = true;

    public BaseHudElementComponent(Configurations configurations, String id, String name) {
        this.configurations = configurations;
        this.id = id;
        this.name = name;
    }

    protected Configuration<TextSettings> getTextSettings(String id) {
        for (Configuration<TextSettings> conf : textSettings) {
            if (conf.getId().equals(id)) return conf;
        }
        Configuration<TextSettings> newConf = configurations.get(this.id + "." + id, "", null, TextSettings.class, DEFAULT_TEXT_SETTINGS);
        textSettings.add(newConf);
        return newConf;
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
