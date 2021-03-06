package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.modules.config.ConfigurationProvider;

public interface Module {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isPaused();

    void setPaused(boolean paused);

    ConflictType[] getConflictTypes();

    ModuleCategory getCategory();

    void setCategory(ModuleCategory category);

    int getKeybind();

    void setKeybind(int key);

    ConfigurationProvider getConfigurationProvider();
}
