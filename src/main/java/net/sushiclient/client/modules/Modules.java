package net.sushiclient.client.modules;

import java.util.ArrayList;
import java.util.List;

public interface Modules {

    default List<Module> getModules(Category category) {
        ArrayList<Module> result = new ArrayList<>();
        for (Module module : getAll()) {
            if (module.getCategory().equals(category))
                result.add(module);
        }
        return result;
    }

    Module getModule(String id);

    ModuleFactory getModuleFactory(String id);

    List<Module> getAll();

    Module addModule(String id, ModuleFactory factory);

    Module cloneModule(String id, String newId);

    void removeModule(String id);

    void save();

    void load();

    void enable();

    void disable();

    void addHandler(ModulesHandler handler);

    boolean removeHandler(ModulesHandler handler);

    List<ModulesHandler> getHandlers();
}
