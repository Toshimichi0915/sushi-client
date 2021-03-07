package net.toshimichi.sushi.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.toshimichi.sushi.modules.config.ConfigurationProvider;
import net.toshimichi.sushi.modules.config.GsonConfigurationProvider;
import net.toshimichi.sushi.modules.render.NoRotateModule;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;

public class GsonModules implements Modules {

    private static final Reflections reflections = new Reflections();
    private static final Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();
    }

    private final File conf;
    private final HashSet<ModuleGroup> groups = new HashSet<>();
    private JsonObject root = new JsonObject();

    public GsonModules(File conf) {
        this.conf = conf;
        addModule("NoRotate", NoRotateModule::new);
    }

    @Override
    public Module getModule(String name) {
        for (ModuleGroup group : groups) {
            if (group.name.equals(name))
                return group.module;
        }
        return null;
    }

    @Override
    public Map<String, Module> getModules() {
        HashMap<String, Module> result = new HashMap<>();
        for (ModuleGroup group : groups) {
            result.put(group.name, group.module);
        }
        return result;
    }

    @Override
    public void addModule(String name, Function<ConfigurationProvider, Module> function) {
        GsonConfigurationProvider provider = new GsonConfigurationProvider(gson, loadJson(name));
        groups.add(new ModuleGroup(name, function.apply(provider), provider));
    }

    @Override
    public void save() {
        try {
            FileUtils.writeStringToFile(conf, gson.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
            root = gson.fromJson(contents, JsonObject.class);
            for (ModuleGroup group : groups) {
                group.provider.load(gson.fromJson(contents, JsonObject.class));
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
        String name;
        Module module;
        GsonConfigurationProvider provider;

        public ModuleGroup(String name, Module module, GsonConfigurationProvider provider) {
            this.name = name;
            this.module = module;
            this.provider = provider;
        }
    }
}
