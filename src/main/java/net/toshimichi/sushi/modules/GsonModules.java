package net.toshimichi.sushi.modules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.GsonConfigurations;
import net.toshimichi.sushi.modules.client.ClickGuiModule;
import net.toshimichi.sushi.modules.client.HudModule;
import net.toshimichi.sushi.modules.combat.VelocityModule;
import net.toshimichi.sushi.modules.movement.PhaseFlyModule;
import net.toshimichi.sushi.modules.movement.PhaseWalkModule;
import net.toshimichi.sushi.modules.movement.SprintModule;
import net.toshimichi.sushi.modules.player.NoRenderModule;
import net.toshimichi.sushi.modules.player.PacketCancellerModule;
import net.toshimichi.sushi.modules.player.TimerModule;
import net.toshimichi.sushi.modules.render.FullBrightModule;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GsonModules implements Modules {

    private static final String ENABLED_TAG = "enabled";
    private static final String FACTORY_TAG = "base";

    private final Gson gson;
    private final int version;
    private final File conf;
    private final Categories categories;
    private final HashSet<GsonModuleFactory> factories = new HashSet<>();
    private final ArrayList<Module> modules = new ArrayList<>();
    private final ArrayList<DefaultModule> defaults = new ArrayList<>();
    private JsonObject root = new JsonObject();
    private boolean enabled;

    public GsonModules(int version, File conf, Categories categories, Gson gson) {
        this.version = version;
        this.conf = conf;
        this.categories = categories;
        this.gson = gson;
        addModuleFactory("click_gui", ClickGuiModule::new, true);
        addModuleFactory("no_render", NoRenderModule::new, true);
        addModuleFactory("velocity", VelocityModule::new, true);
        addModuleFactory("hud", HudModule::new, true);
        addModuleFactory("full_bright", FullBrightModule::new, true);
        addModuleFactory("sprint", SprintModule::new, true);
        addModuleFactory("packet_canceller", PacketCancellerModule::new, true);
        addModuleFactory("phase_fly", PhaseFlyModule::new, true);
        addModuleFactory("phase_walk", PhaseWalkModule::new, true);
        addModuleFactory("timer", TimerModule::new, true);
    }

    private void addModuleFactory(String id, ModuleConstructor constructor, boolean isDefault) {
        factories.add(new GsonModuleFactory(id, constructor));
        if (isDefault) defaults.add(new DefaultModule(id, id));
    }

    @Override
    public Module getModule(String id) {
        for (Module module : modules) {
            if (module.getId().equals(id))
                return module;
        }
        return null;
    }

    @Override
    public ModuleFactory getModuleFactory(String id) {
        for (ModuleFactory factory : factories) {
            if (factory.getId().equals(id))
                return factory;
        }
        return null;
    }

    @Override
    public List<Module> getAll() {
        return new ArrayList<>(modules);
    }

    @Override
    public Module addModule(String id, ModuleFactory factory) {
        GsonConfigurations provider = new GsonConfigurations(gson);
        JsonObject object = loadJson(id);
        object.add(FACTORY_TAG, new JsonPrimitive(factory.getId()));
        if (!object.has(ENABLED_TAG)) object.add(ENABLED_TAG, new JsonPrimitive(false));
        provider.load(object);
        Module module = factory.getConstructor().newModule(id, this, categories, provider, factory);
        modules.add(module);
        return module;
    }

    @Override
    public Module cloneModule(String id, String newId) {
        Module original = getModule(id);
        if (original == null) return null;

        // deep copy JsonElement
        root.add(newId, gson.fromJson(gson.toJson(root.get(id)), JsonElement.class));
        return addModule(newId, original.getModuleFactory());
    }

    @Override
    public void removeModule(String id) {
        Module module = getModule(id);
        if (module == null) return;
        modules.remove(module);
        root.remove(module.getId());
    }

    @Override
    public void save() {
        try {
            JsonObject savedRoot = new JsonObject();
            for (Module module : modules) {
                Configurations conf = module.getConfigurations();
                if (!(module.getConfigurations() instanceof GsonConfigurations)) continue;
                JsonObject obj = ((GsonConfigurations) conf).save();
                obj.add(FACTORY_TAG, new JsonPrimitive(module.getModuleFactory().getId()));
                savedRoot.add(module.getId(), ((GsonConfigurations) conf).save());
            }
            FileUtils.writeStringToFile(conf, gson.toJson(savedRoot), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateConfig(JsonObject root) {
        for (DefaultModule module : defaults) {
            root.remove(module.id);
            root.add(module.id, new JsonObject());
            addModule(module.id, getModuleFactory(module.factory));
        }
    }

    @Override
    public void load() {
        try {
            modules.clear();
            if (!conf.exists()) {
                updateConfig(new JsonObject());
            } else {
                String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
                root = gson.fromJson(contents, JsonObject.class);
                if (version < Sushi.getVersion()) updateConfig(root);
            }
            for (Map.Entry<String, JsonElement> entry : new HashSet<>(root.getAsJsonObject().entrySet())) {
                JsonObject moduleJson = entry.getValue().getAsJsonObject();
                String factoryId = moduleJson.getAsJsonPrimitive(FACTORY_TAG).getAsString();
                ModuleFactory factory = getModuleFactory(factoryId);
                if (factory == null) {
                    root.remove(entry.getKey());
                } else if (getModule(entry.getKey()) == null) {
                    addModule(entry.getKey(), getModuleFactory(factoryId));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        for (Module module : modules) {
            if (!(module.getConfigurations() instanceof GsonConfigurations)) continue;
            JsonObject object = ((GsonConfigurations) module.getConfigurations()).save();
            JsonPrimitive enabledTag = object.getAsJsonPrimitive(ENABLED_TAG);
            if (enabledTag != null && enabledTag.getAsBoolean())
                module.setEnabled(true);
        }
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        for (Module module : modules) {
            if (!(module.getConfigurations() instanceof GsonConfigurations)) continue;
            JsonObject object = ((GsonConfigurations) module.getConfigurations()).save();
            if (!module.isTemporary())
                object.add(ENABLED_TAG, new JsonPrimitive(module.isEnabled()));
            module.setEnabled(false);
        }
    }

    @Override
    public void reload() {
        for (Module module : modules) {
            if (!(module.getConfigurations() instanceof GsonConfigurations)) continue;
            JsonObject object = ((GsonConfigurations) module.getConfigurations()).save();
            object.add(ENABLED_TAG, new JsonPrimitive(module.isEnabled()));
        }
    }

    private JsonObject loadJson(String id) {
        JsonObject object = root.getAsJsonObject(id);
        if (object == null) {
            object = new JsonObject();
            root.add(id, object);
        }
        return object;
    }

    private static class DefaultModule {
        String id;
        String factory;

        DefaultModule(String id, String factory) {
            this.id = id;
            this.factory = factory;
        }
    }
}
