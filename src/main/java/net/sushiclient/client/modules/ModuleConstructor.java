package net.sushiclient.client.modules;

import net.sushiclient.client.config.RootConfigurations;

@FunctionalInterface
public interface ModuleConstructor {
    Module newModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory);
}
