package net.toshimichi.sushi.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GsonConfigurations implements Configurations {

    private final Gson gson;
    private JsonObject root;
    private final ArrayList<GsonConfiguration<?>> list = new ArrayList<>();
    private final ArrayList<ConfigurationCategory> categories = new ArrayList<>();
    private final HashMap<String, Object> defaults = new HashMap<>();

    public GsonConfigurations(Gson gson) {
        this.gson = gson;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Configuration<T> get(String id, String name, String description, Class<T> tClass, T defaultValue, Supplier<Boolean> isValid, String category, boolean temporary) {
        if (!temporary)
            defaults.put(id, defaultValue);
        for (GsonConfiguration<?> loaded : list) {
            if (loaded.getId().equals(id)) {
                if (!loaded.getValueClass().equals(tClass))
                    throw new IllegalArgumentException("Supplied class is not valid");
                return (Configuration<T>) loaded;
            }
        }
        GsonConfiguration<T> conf = new GsonConfiguration<>(id, name, description, tClass, defaultValue, this, isValid, category, temporary);
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
        this.root = object;
    }

    private void setRawValue(JsonObject obj, String key, Object o, boolean override) {
        if (key.contains(".")) {
            String child = key.split("\\.")[0];
            JsonElement childObj = obj.get(child);
            if (childObj == null || !childObj.isJsonObject()) {
                childObj = new JsonObject();
                obj.add(child, childObj);
            }
            setRawValue(childObj.getAsJsonObject(), key.replaceFirst(child + "\\.", ""), o, override);
        } else if (obj.get(key) == null || override) {
            obj.add(key, gson.toJsonTree(o));
        }
    }

    public JsonObject save() {
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            setRawValue(root, entry.getKey(), entry.getValue(), false);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    private <T> T getRawValue(JsonObject object, String id, Class<T> tClass, boolean checkDefault) {
        try {
            if (id.contains(".")) {
                String key = id.split("\\.")[0];
                JsonElement element = object.get(key);
                if (element != null && element.isJsonObject()) {
                    T rawValue = getRawValue(element.getAsJsonObject(), id.replaceFirst(key + "\\.", ""), tClass, false);
                    if (rawValue != null) return rawValue;
                }
            } else {
                JsonElement element = object.get(id);
                if (element != null) return gson.fromJson(object.get(id), tClass);
            }
        } catch (JsonParseException e) {
            // use default
        }
        if (checkDefault) {
            Object result = this.defaults.get(id);
            if (result != null && result.getClass().isAssignableFrom(tClass)) return (T) result;
        }
        return null;
    }

    protected <T> T getRawValue(String id, Class<T> tClass) {
        return getRawValue(root, id, tClass, true);
    }

    protected void setRawValue(String id, Object o) {
        setRawValue(root, id, o, true);
    }

}
