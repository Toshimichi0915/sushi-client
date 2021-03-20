package net.toshimichi.sushi.modules;

import net.toshimichi.sushi.config.Configurations;

@FunctionalInterface
public interface ModuleConstructor {
    Module newModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory);
}
