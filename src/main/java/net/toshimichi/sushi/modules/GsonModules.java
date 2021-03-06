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
import java.util.HashSet;
import java.util.Set;
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
    private final GsonConfigurationProvider provider = new GsonConfigurationProvider(gson);
    private final HashSet<Module> modules = new HashSet<>();

    public GsonModules(File conf) {
        this.conf = conf;
        addModule(NoRotateModule::new);
    }

    @Override
    public Module getModule(Class<? extends Module> moduleClass) {
        for (Module module : modules) {
            if (reflections.getSubTypesOf(module.getClass()).contains(moduleClass))
                return module;
        }
        return null;
    }

    @Override
    public Set<Module> getModules() {
        return new HashSet<>(modules);
    }

    @Override
    public void addModule(Function<ConfigurationProvider, Module> function) {
        modules.add(function.apply(provider));
    }

    @Override
    public void save() {
        try {
            FileUtils.writeStringToFile(conf, gson.toJson(provider.save()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
            provider.load(gson.fromJson(contents, JsonObject.class));
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
    }
}
