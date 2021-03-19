package net.toshimichi.sushi.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Modules {

    default List<Module> getModules(Category category) {
        ArrayList<Module> result = new ArrayList<>();
        for(Module module : getAll()) {
            if(module.getCategory().equals(category))
                result.add(module);
        }
        return result;
    }

    Module getModule(String name);

    List<Module> getAll();

    void addModule(String name, ModuleFactory factory);

    void save();

    void load();
}
