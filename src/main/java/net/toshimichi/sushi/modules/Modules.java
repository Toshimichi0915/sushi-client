package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.modules.config.ConfigurationProvider;

import java.util.Map;
import java.util.function.Function;

public interface Modules {

    Module getModule(String name);

    Map<String, Module> getModules();

    void addModule(String name, Function<ConfigurationProvider, Module> function);

    void save();

    void load();
}
