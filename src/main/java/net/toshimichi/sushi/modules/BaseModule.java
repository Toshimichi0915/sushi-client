package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;

abstract public class BaseModule implements Module {

    private final String id;
    private final Configurations provider;
    private final Modules modules;
    private final Categories categories;
    private final Configuration<String> name;
    private final Configuration<String> category;
    private final Configuration<Integer> keybind;
    private final ModuleFactory factory;
    private boolean isEnabled;
    private boolean isPaused;

    public BaseModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        this.id = id;
        this.provider = provider;
        this.modules = modules;
        this.categories = categories;
        this.factory = factory;
        this.name = provider.get("name", "Name", "Module Name", String.class, getDefaultName());
        this.category = provider.get("category", "Category", "Module Category", String.class, getDefaultCategory().getName());
        this.keybind = provider.get("keybind", "Keybind", "Keybind for this module", Integer.class, getDefaultKeybind());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name.getValue();
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
    }

    public void onDisable() {
    }

    @Override
    public Configurations getConfigurations() {
        return provider;
    }

    @Override
    public ConflictType[] getConflictTypes() {
        return new ConflictType[0];
    }

    @Override
    public Category getCategory() {
        Category result = categories.getModuleCategory(category.getValue());
        if (result != null) return result;
        return getDefaultCategory();
    }

    @Override
    public void setCategory(Category category) {
        this.category.setValue(category.getName());
    }

    @Override
    public int getKeybind() {
        return keybind.getValue();
    }

    @Override
    public void setKeybind(int key) {
        keybind.setValue(key);
    }

    @Override
    public ModuleFactory getModuleFactory() {
        return factory;
    }

    abstract public String getDefaultName();

    abstract public int getDefaultKeybind();

    abstract public Category getDefaultCategory();
}
