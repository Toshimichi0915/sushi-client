package net.toshimichi.sushi.modules;

import net.minecraftforge.common.MinecraftForge;
import net.toshimichi.sushi.modules.config.Configuration;
import net.toshimichi.sushi.modules.config.ConfigurationProvider;

abstract public class BaseModule implements Module {

    private final ConfigurationProvider provider;
    private final Configuration<ModuleCategory> category;
    private final Configuration<Integer> keybind;
    private boolean isEnabled;
    private boolean isPaused;

    public BaseModule(ConfigurationProvider provider) {
        this.provider = provider;
        this.category = provider.getConfiguration("category", ModuleCategory.class, getDefaultCategory());
        this.keybind = provider.getConfiguration("keybind", Integer.class, getDefaultKeybind());
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!isPaused) {
            if (!this.isEnabled && enabled)
                onEnable();
            else if (this.isEnabled && !enabled)
                onDisable();
        }
        this.isEnabled = enabled;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void setPaused(boolean paused) {
        if (!this.isPaused && paused && isEnabled) {
            onDisable();
        } else if (this.isPaused && !paused && !isEnabled) {
            onEnable();
        }
        this.isPaused = paused;
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public ConfigurationProvider getConfigurationProvider() {
        return provider;
    }

    @Override
    public ConflictType[] getConflictTypes() {
        return new ConflictType[0];
    }

    abstract public ModuleCategory getDefaultCategory();

    @Override
    public ModuleCategory getCategory() {
        return category.getValue();
    }

    @Override
    public void setCategory(ModuleCategory category) {
        this.category.setValue(category);
    }

    abstract public int getDefaultKeybind();

    @Override
    public int getKeybind() {
        return keybind.getValue();
    }

    @Override
    public void setKeybind(int key) {
        keybind.setValue(key);
    }
}
