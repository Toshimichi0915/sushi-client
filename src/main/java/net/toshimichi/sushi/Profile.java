package net.toshimichi.sushi;

import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.Modules;

public interface Profile {
    Modules getModules();

    Categories getCategories();

    void load();

    void save();

}
