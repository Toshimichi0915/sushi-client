package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.modules.config.ConfigurationProvider;

import java.util.Set;
import java.util.function.Function;

public interface Modules {

    Module getModule(Class<? extends Module> moduleClass);

    Set<Module> getModules();

    void addModule(Function<ConfigurationProvider, Module> function);

    void save();

    void load();
}
