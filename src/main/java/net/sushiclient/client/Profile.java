package net.sushiclient.client;

import net.sushiclient.client.command.Logger;
import net.sushiclient.client.modules.Categories;
import net.sushiclient.client.modules.Modules;

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
