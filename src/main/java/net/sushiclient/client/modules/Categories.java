package net.sushiclient.client.modules;

import java.util.List;

public interface Categories {

    Category getModuleCategory(String id);

    List<Category> getAll();

    void load();

    void save();
}
