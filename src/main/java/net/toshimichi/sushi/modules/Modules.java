package net.toshimichi.sushi.modules;

import java.util.Map;

public interface Modules {

    Module getModule(String name);

    Map<String, Module> getAll();

    void addModule(String name, ModuleFactory factory);

    void save();

    void load();
}
