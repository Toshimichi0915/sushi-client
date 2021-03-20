package net.toshimichi.sushi.modules;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.GsonConfigurations;
import net.toshimichi.sushi.modules.client.ClickGuiModule;
import net.toshimichi.sushi.modules.render.NoRotateModule;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GsonModules implements Modules {

    private static final String FACTORY_TAG = "base";

    private final Gson gson;
    private final File conf;
    private final Categories categories;
    private final HashSet<GsonModuleFactory> factories = new HashSet<>();
    private final ArrayList<Module> modules = new ArrayList<>();
    private final ArrayList<DefaultModule> defaults = new ArrayList<>();
    private JsonObject root = new JsonObject();

    public GsonModules(File conf, Categories categories, Gson gson) {
        this.conf = conf;
        this.categories = categories;
        this.gson = gson;
        addModuleFactory("no_rotate", NoRotateModule::new);
        addModuleFactory("clickgui", ClickGuiModule::new);

        addDefaultModule("no_rotate", "no_rotate");
        addDefaultModule("clickgui", "clickgui");
    }

    private void addDefaultModule(String id, String factory) {
        defaults.add(new DefaultModule(id, factory));
    }

    private void addModuleFactory(String id, ModuleConstructor constructor) {
        factories.add(new GsonModuleFactory(id, constructor));
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
    public void addModule(String id, ModuleFactory factory) {
        GsonConfigurations provider = new GsonConfigurations(gson);
        JsonObject object = loadJson(id);
        object.add(FACTORY_TAG, new JsonPrimitive(factory.getId()));
        provider.load(object);
        modules.add(factory.getConstructor().newModule(id, this, categories, provider, factory));
    }

    @Override
    public void save() {
        try {
            JsonObject savedRoot = new JsonObject();
            for (Module module : modules) {
                Configurations conf = module.getConfigurations();
                if (!(module.getConfigurations() instanceof GsonConfigurations)) continue;
                savedRoot.add(module.getId(), ((GsonConfigurations) conf).save());
            }
            FileUtils.writeStringToFile(conf, gson.toJson(savedRoot), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            if (!conf.exists()) {
                root = new JsonObject();
                for (DefaultModule module : defaults) {
                    root.add(module.id, new JsonObject());
                    addModule(module.id, getModuleFactory(module.factory));
                }
            } else {
                String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
                root = gson.fromJson(contents, JsonObject.class);
            }
            modules.clear();
            for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
                String factoryId = entry.getValue().getAsJsonObject().getAsJsonPrimitive(FACTORY_TAG).getAsString();
                addModule(entry.getKey(), getModuleFactory(factoryId));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject loadJson(String name) {
        JsonObject object = root.getAsJsonObject(name);
        if (object == null) {
            object = new JsonObject();
            root.add(name, object);
        }
        return object;
    }

    private static class DefaultModule {
        String id;
        String factory;

        public DefaultModule(String id, String factory) {
            this.id = id;
            this.factory = factory;
        }
    }
}
