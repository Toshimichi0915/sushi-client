package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.config.RootConfigurations;

@FunctionalInterface
public interface ModuleConstructor {
    Module newModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory);
}
