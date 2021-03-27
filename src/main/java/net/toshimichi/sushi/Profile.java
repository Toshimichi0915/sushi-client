package net.toshimichi.sushi;

import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.modules.Categories;
import net.toshimichi.sushi.modules.Modules;

public interface Profile {

    Theme getTheme();

    Modules getModules();

    Categories getCategories();

    char getPrefix();

    MessageHandler getMessageHandler();

    void load();

    void save();

}
