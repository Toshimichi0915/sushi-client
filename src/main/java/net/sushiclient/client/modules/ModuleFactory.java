package net.sushiclient.client.modules;

public interface ModuleFactory {
    String getId();

    ModuleConstructor getConstructor();
}
