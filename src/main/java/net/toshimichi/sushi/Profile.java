package net.toshimichi.sushi;

import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.Modules;

public interface Profile {

    Modules getModules();

    Categories getCategories();

    char getPrefix();

    void setPrefix(char prefix);

    MessageHandler getMessageHandler();

    void load();

    void save();

}
