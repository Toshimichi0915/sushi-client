package net.toshimichi.sushi.modules.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class GsonConfigurationProvider implements ConfigurationProvider {

    private final Gson gson;
    private JsonObject object;

    public GsonConfigurationProvider(Gson gson, JsonObject object) {
        this.gson = gson;
        this.object = object;
    }

    public GsonConfigurationProvider(Gson gson) {
        this(gson, new JsonObject());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Configuration<T> getConfiguration(String name, Class<T> tClass, T defaultValue) {
        if (getRawValue(name, tClass) == null)
            setRawValue(name, defaultValue);
        return new GsonConfiguration<>(name, tClass, this);
    }

    public void load(JsonObject object) {
        this.object = object;
    }

    public JsonObject save() {
        return object;
    }

    protected <T> T getRawValue(String name, Class<T> tClass) {
        JsonElement element = object.get(name);
        if (element == null) {
            return null;
        } else {
            try {
                return gson.fromJson(object.get(name), tClass);
            } catch (JsonParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    protected void setRawValue(String name, Object o) {
        object.add(name, gson.toJsonTree(o));
    }

}
