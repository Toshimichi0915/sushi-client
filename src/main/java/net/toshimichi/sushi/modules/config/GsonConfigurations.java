package net.toshimichi.sushi.modules.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class GsonConfigurations implements Configurations {

    private final Gson gson;
    private JsonObject object;
    private final ArrayList<GsonConfiguration<?>> list = new ArrayList<>();
    private final ArrayList<ConfigurationCategory> categories = new ArrayList<>();

    public GsonConfigurations(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Configuration<T> get(String id, String name, String description, Class<T> tClass, T defaultValue, Supplier<Boolean> isValid, String category) {
        if (getRawValue(id, tClass) == null)
            setRawValue(id, defaultValue);
        for (GsonConfiguration<?> loaded : list) {
            if (loaded.getId().equals(id)) {
                if (!loaded.getValueClass().equals(tClass))
                    throw new IllegalArgumentException("Supplied class is not valid");
                return (Configuration<T>) loaded;
            }
        }
        GsonConfiguration<T> conf = new GsonConfiguration<>(id, name, description, tClass, this, isValid, category);
        list.add(conf);
        return conf;
    }

    @Override
    public List<Configuration<?>> getAll() {
        return new ArrayList<>(list);
    }

    @Override
    public ConfigurationCategory newCategory(String id, String name, String description) {
        GsonConfigurationCategory category = new GsonConfigurationCategory(id, name, description);
        categories.add(category);
        return category;
    }

    @Override
    public List<ConfigurationCategory> getCategories() {
        return categories;
    }

    public void load(JsonObject object) {
        this.object = object;
    }

    public JsonObject save() {
        return object;
    }

    protected <T> T getRawValue(String id, Class<T> tClass) {
        JsonElement element = object.get(id);
        if (element == null) {
            return null;
        } else {
            try {
                return gson.fromJson(object.get(id), tClass);
            } catch (JsonParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    protected void setRawValue(String id, Object o) {
        object.add(id, gson.toJsonTree(o));
    }

}
