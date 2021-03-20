package net.toshimichi.sushi.modules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.toshimichi.sushi.modules.client.ClickGuiModule;
import net.toshimichi.sushi.modules.config.GsonConfigurations;
import net.toshimichi.sushi.modules.render.NoRotateModule;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class GsonModules implements Modules {


    private final Gson gson;
    private final File conf;
    private final Categories categories;
    private final HashSet<ModuleGroup> groups = new HashSet<>();
    private JsonObject root = new JsonObject();

    public GsonModules(File conf, Categories categories, Gson gson) {
        this.conf = conf;
        this.categories = categories;
        this.gson = gson;
        addModule("norotate", "NoRotate", NoRotateModule::new);
        addModule("clickgui", "ClickGUI", ClickGuiModule::new);
    }

    @Override
    public Module getModule(String id) {
        for (ModuleGroup group : groups) {
            if (group.module.getId().equals(id))
                return group.module;
        }
        return null;
    }

    @Override
    public List<Module> getAll() {
        return groups.stream()
                .map(group -> group.module)
                .collect(Collectors.toList());
    }

    @Override
    public void addModule(String id, String name, ModuleFactory factory) {
        GsonConfigurations provider = new GsonConfigurations(gson);
        provider.load(loadJson(id));
        groups.add(new ModuleGroup(factory.newModule(id, name, this, categories, provider), provider));
    }

    @Override
    public void save() {
        try {
            JsonObject savedRoot = new JsonObject();
            for (ModuleGroup group : groups) {
                savedRoot.add(group.module.getId(), group.provider.save());
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
            } else {
                String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
                root = gson.fromJson(contents, JsonObject.class);
            }
            for (ModuleGroup group : groups) {
                group.provider.load(loadJson(group.module.getId()));
            }
        } catch (IOException | JsonParseException e) {
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

    private static class ModuleGroup {
        Module module;
        GsonConfigurations provider;

        public ModuleGroup(Module module, GsonConfigurations provider) {
            this.module = module;
            this.provider = provider;
        }
    }
}
