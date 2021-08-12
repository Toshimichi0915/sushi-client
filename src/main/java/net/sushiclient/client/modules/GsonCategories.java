package net.sushiclient.client.modules;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GsonCategories implements Categories {

    private final Gson gson;
    private static final ArrayList<Category> defaultCategories = new ArrayList<>();

    static {
        defaultCategories.add(Category.COMBAT);
        defaultCategories.add(Category.MOVEMENT);
        defaultCategories.add(Category.RENDER);
        defaultCategories.add(Category.PLAYER);
        defaultCategories.add(Category.WORLD);
        defaultCategories.add(Category.CLIENT);
    }

    private final ArrayList<GsonCategory> categories = new ArrayList<>();
    private final File conf;

    public GsonCategories(File conf, Gson gson) {
        this.conf = conf;
        this.gson = gson;
    }

    @Override
    public Category getModuleCategory(String name) {
        for (Category element : getAll()) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }

    @Override
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>(defaultCategories);
        list.addAll(categories);
        return list;
    }

    @Override
    public void load() {
        try {
            if (!conf.exists()) return;
            String contents = FileUtils.readFileToString(conf, StandardCharsets.UTF_8);
            JsonArray array = gson.fromJson(contents, JsonArray.class);
            for (JsonElement element : array) {
                categories.add(gson.fromJson(element, GsonCategory.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try {
            FileUtils.writeStringToFile(conf, gson.toJson(categories), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
