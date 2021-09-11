package net.sushiclient.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.gui.hud.ElementConstructor;
import net.sushiclient.client.gui.hud.ElementFactory;

import java.util.ArrayList;

abstract public class BaseModule implements Module {

    private final String id;
    private final RootConfigurations provider;
    private final Modules modules;
    private final Categories categories;
    private final Configuration<String> name;
    private final Configuration<String> category;
    private final Configuration<Keybind> keybind;
    private final Configuration<Boolean> temporary;
    private final Configuration<Boolean> visible;
    private final Configuration<Boolean> toggleNotification;
    private final ModuleFactory factory;
    private final ArrayList<ElementFactory> hudElementFactories;
    private final ArrayList<ModuleHandler> handlers;
    private Category currentCategory;
    private boolean enabled;
    private boolean paused;

    public BaseModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        this.id = id;
        this.provider = provider;
        this.modules = modules;
        this.categories = categories;
        this.factory = factory;
        ConfigurationCategory commonCategory = provider.getCategory("common", "Common Settings", "Common settings for most modules");
        commonCategory.get("id", "Module ID", "Module ID", String.class, id, () -> true, true, 80000);
        this.name = commonCategory.get("name", "Module Name", "Module name", String.class, getDefaultName(), () -> true, false, 81000);
        this.category = commonCategory.get("category", "Module Category", "Module category", String.class, getDefaultCategory().getName(), () -> true, false, 82000);
        this.keybind = provider.get("keybind", "Module Keybind", "Keybind for this module", Keybind.class, getDefaultKeybind(), () -> true, false, 83000);
        this.temporary = commonCategory.get("temporary", "Temporary Module", null, Boolean.class, isTemporaryByDefault(), () -> true, false, 84000);
        this.visible = commonCategory.get("visible", "Visible", null, Boolean.class, isVisibleByDefault(), () -> true, false, 85000);
        this.toggleNotification = commonCategory.get("toggle_notification", "Toggle Notification", null, Boolean.class, false, () -> true, false, 86000);
        this.handlers = new ArrayList<>();
        commonCategory.get("reset", "Reset Settings", null, Runnable.class, provider::reset, () -> true, true, 86000);
        currentCategory = categories.getModuleCategory(category.getValue());
        if (currentCategory == null) currentCategory = getDefaultCategory();
        category.addHandler(it -> {
            Category newCategory = categories.getModuleCategory(it);
            if (newCategory == null) newCategory = getDefaultCategory();
            if (!newCategory.equals(currentCategory)) {
                Category f = newCategory;
                handlers.forEach(it2 -> it2.setCategory(f));
                currentCategory = f;
            }
        });

        hudElementFactories = new ArrayList<>();
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
    public boolean isTemporary() {
        return temporary.getValue();
    }

    @Override
    public boolean isVisible() {
        return visible.getValue();
    }

    @Override
    public void setVisible(boolean v) {
        visible.setValue(v);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!paused) {
            Logger handler = Sushi.getProfile().getLogger();
            if (!this.enabled && enabled) {
                handlers.forEach(it -> it.setEnabled(true));
                onEnable();
                if (toggleNotification.getValue()) {
                    handler.send(LogLevel.INFO, "Enabled " + getName());
                }
            } else if (this.enabled && !enabled) {
                handlers.forEach(it -> it.setEnabled(false));
                onDisable();
                if (toggleNotification.getValue()) {
                    handler.send(LogLevel.INFO, "Disabled " + getName());
                }
            }
        }
        this.enabled = enabled;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void setPaused(boolean paused) {
        if (!this.paused && paused && enabled) {
            handlers.forEach(it -> it.setPaused(true));
            onDisable();
        } else if (this.paused && !paused && enabled) {
            handlers.forEach(it -> it.setPaused(false));
            onEnable();
        }
        this.paused = paused;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    @Override
    public RootConfigurations getConfigurations() {
        return provider;
    }

    @Override
    public ConflictType[] getConflictTypes() {
        return new ConflictType[0];
    }

    @Override
    public Category getCategory() {
        return currentCategory;
    }

    @Override
    public void setCategory(Category category) {
        this.category.setValue(category.getName());
    }

    @Override
    public Keybind getKeybind() {
        return keybind.getValue();
    }

    @Override
    public void setKeybind(Keybind bind) {
        handlers.forEach(it -> it.setKeybind(bind));
        keybind.setValue(bind);
    }

    @Override
    public ModuleFactory getModuleFactory() {
        return factory;
    }

    @Override
    public ElementFactory[] getElementFactories() {
        return hudElementFactories.toArray(new ElementFactory[0]);
    }

    abstract public String getDefaultName();

    protected void addElementFactory(ElementConstructor constructor, String id, String name) {
        hudElementFactories.add(new BaseElementFactory(constructor, id, name));
    }

    public Keybind getDefaultKeybind() {
        return new Keybind(ActivationType.TOGGLE);
    }

    abstract public Category getDefaultCategory();

    protected boolean isTemporaryByDefault() {
        return false;
    }

    protected boolean isVisibleByDefault() {
        return true;
    }

    protected Minecraft getClient() {
        return Minecraft.getMinecraft();
    }

    protected EntityPlayerSP getPlayer() {
        return getClient().player;
    }

    protected PlayerControllerMP getController() {
        return getClient().playerController;
    }

    protected WorldClient getWorld() {
        return getClient().world;
    }

    protected NetHandlerPlayClient getConnection() {
        return getPlayer().connection;
    }

    @Override
    public void addHandler(ModuleHandler handler) {
        handlers.add(handler);
    }

    @Override
    public boolean removeHandler(ModuleHandler handler) {
        return handlers.remove(handler);
    }

    @Override
    public ArrayList<ModuleHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }

    private static class BaseElementFactory implements ElementFactory {

        private final ElementConstructor constructor;
        private final String id;
        private final String name;

        public BaseElementFactory(ElementConstructor constructor, String id, String name) {
            this.constructor = constructor;
            this.id = id;
            this.name = name;
        }

        @Override
        public ElementConstructor getElementConstructor() {
            return constructor;
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
}
