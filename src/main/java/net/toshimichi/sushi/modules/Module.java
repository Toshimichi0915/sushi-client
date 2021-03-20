package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.config.Configurations;

public interface Module {

    String getId();

    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isPaused();

    void setPaused(boolean paused);

    ConflictType[] getConflictTypes();

    Category getCategory();

    void setCategory(Category category);

    int getKeybind();

    void setKeybind(int key);

    Configurations getConfigurations();
}
