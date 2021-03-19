package net.toshimichi.sushi.modules;

import java.util.List;

public interface Categories {

    Category getModuleCategory(String name);

    List<Category> getAll();

    void addModuleCategory(Category category);

    void load();

    void save();
}
