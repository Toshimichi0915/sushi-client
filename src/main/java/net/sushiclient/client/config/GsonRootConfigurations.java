package net.sushiclient.client.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonRootConfigurations extends GsonConfigurations implements RootConfigurations {

    private final Gson gson;
    private final ArrayList<ConfigurationCategory> categories = new ArrayList<>();
    private final HashMap<String, Object> defaults = new HashMap<>();
    private JsonObject root;

    public GsonRootConfigurations(Gson gson) {
        this.gson = gson;
    }

    public void load(JsonObject object) {
        this.root = object;
    }

    public JsonObject save() {
        getAll(true).forEach(it -> ((GsonConfiguration<?>) it).save());
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            setRawValue(root, entry.getKey(), entry.getValue(), false);
        }
        return root;
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

    private <T> T getRawValue(JsonObject object, String id, Class<T> tClass) {
        try {
            if (id.contains(".")) {
                String key = id.split("\\.")[0];
                JsonElement element = object.get(key);
                if (element != null && element.isJsonObject()) {
                    T rawValue = getRawValue(element.getAsJsonObject(), id.replaceFirst(key + "\\.", ""), tClass);
                    if (rawValue != null) return rawValue;
                }
            } else {
                JsonElement element = object.get(id);
                if (element != null) {
                    return gson.fromJson(object.get(id), tClass);
                }
            }
        } catch (JsonParseException e) {
            // use default
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getRawValue(String id, Class<T> tClass) {
        T rawValue = getRawValue(root, id, tClass);
        if (rawValue != null) return rawValue;

        Object result = defaults.get(id);
        if (result != null && tClass.isAssignableFrom(result.getClass())) return (T) result;
        else return null;
    }

    protected void setRawValue(String id, Object o) {
        setRawValue(root, id, o, true);
    }

    protected void putDefault(String id, Object obj) {
        defaults.put(id, obj);
    }

    @Override
    public List<ConfigurationCategory> getCategories() {
        return categories;
    }

    @Override
    public ConfigurationCategory getCategory(String id, String name, String description) {
        GsonConfigurationCategory category = new GsonConfigurationCategory(this, id, name, description);
        categories.add(category);
        getHandlers().forEach(it -> {
            if (it instanceof RootConfigurationsHandler) {
                ((RootConfigurationsHandler) it).getCategory(category);
            }
        });
        return category;
    }

    @Override
    protected ConfigurationCategory getConfigurationCategory() {
        return null;
    }

    @Override
    protected GsonRootConfigurations getRoot() {
        return this;
    }
}
