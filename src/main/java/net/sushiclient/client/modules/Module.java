package net.sushiclient.client.modules;

import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.hud.ElementFactory;

public interface Module {

    String getId();

    String getName();

    boolean isTemporary();

    boolean isVisible();

    void setVisible(boolean visible);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isPaused();

    void setPaused(boolean paused);

    ConflictType[] getConflictTypes();

    Category getCategory();

    void setCategory(Category category);

    Keybind getKeybind();

    void setKeybind(Keybind bind);

    RootConfigurations getConfigurations();

    ModuleFactory getModuleFactory();

    ElementFactory[] getElementFactories();
}
