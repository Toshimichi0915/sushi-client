package net.toshimichi.sushi.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.config.RootConfigurations;

abstract public class BaseModule implements Module {

    private final String id;
    private final RootConfigurations provider;
    private final Modules modules;
    private final Categories categories;
    private final Configuration<String> name;
    private final Configuration<String> category;
    private final Configuration<Keybind> keybind;
    private final Configuration<Boolean> isTemporary;
    private final ModuleFactory factory;
    private boolean isEnabled;
    private boolean isPaused;

    public BaseModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        this.id = id;
        this.provider = provider;
        this.modules = modules;
        this.categories = categories;
        this.factory = factory;
        ConfigurationCategory commonCategory = provider.getCategory("common", "Common Settings", "Common settings for most modules");
        this.name = commonCategory.get("name", "Module Name", "Module name", String.class, getDefaultName(), () -> true, false, 80000);
        this.category = commonCategory.get("category", "Module Category", "Module category", String.class, getDefaultCategory().getName(), () -> true, false, 81000);
        this.keybind = provider.get("keybind", "Module Keybind", "Keybind for this module", Keybind.class, getDefaultKeybind(), () -> true, false, 82000);
        this.isTemporary = commonCategory.get("temporary", "Temporary Module", null, Boolean.class, isTemporaryByDefault(), () -> true, false, 83000);
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
        return isTemporary.getValue();
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
    public RootConfigurations getConfigurations() {
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
    public Keybind getKeybind() {
        return keybind.getValue();
    }

    @Override
    public void setKeybind(Keybind bind) {
        keybind.setValue(bind);
    }

    @Override
    public ModuleFactory getModuleFactory() {
        return factory;
    }

    abstract public String getDefaultName();

    public Keybind getDefaultKeybind() {
        return new Keybind(ActivationType.TOGGLE);
    }

    abstract public Category getDefaultCategory();

    protected boolean isTemporaryByDefault() {
        return false;
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
}
