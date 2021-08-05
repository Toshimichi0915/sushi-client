package net.toshimichi.sushi;

import net.toshimichi.sushi.command.Logger;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.Modules;

public interface Profile {

    int getVersion();

    Modules getModules();

    Categories getCategories();

    char getPrefix();

    void setPrefix(char prefix);

    Logger getLogger();

    void load();

    void save();

}
