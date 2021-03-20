package net.toshimichi.sushi.modules;

public interface ModuleFactory {
    String getId();

    ModuleConstructor getConstructor();
}
