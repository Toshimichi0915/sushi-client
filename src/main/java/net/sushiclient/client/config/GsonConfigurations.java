package net.sushiclient.client.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

abstract public class GsonConfigurations implements Configurations {

    private static final int PRIORITY_DELTA = 100;

    private final ArrayList<GsonConfiguration<?>> list = new ArrayList<>();
    private final ArrayList<ConfigurationsHandler> handlers = new ArrayList<>();
    private int counter;

    @SuppressWarnings("unchecked")
    @Override
    public <T> Configuration<T> get(String id, String name, String description, Class<T> tClass, T defaultValue, Supplier<Boolean> valid, boolean temporary, int priority) {
        if (priority == 0) priority = counter++ * PRIORITY_DELTA;
        if (!temporary) getRoot().putDefault(id, defaultValue);
        for (GsonConfiguration<?> loaded : list) {
            if (loaded.getId().equals(id)) {
                if (!loaded.getValueClass().equals(tClass))
                    throw new IllegalArgumentException("Supplied class is not valid");
                return (Configuration<T>) loaded;
            }
        }
        GsonConfiguration<T> conf = new GsonConfiguration<>(id, name, description, tClass, defaultValue, getRoot(), valid, getConfigurationCategory(), temporary, priority);
        list.add(conf);
        handlers.forEach(it -> it.get(conf));
        return conf;
    }

    @Override
    public List<Configuration<?>> getAll() {
        return new ArrayList<>(list);
    }

    abstract protected ConfigurationCategory getConfigurationCategory();

    abstract protected GsonRootConfigurations getRoot();

    @Override
    public void addHandler(ConfigurationsHandler handler) {
        handlers.add(handler);
    }

    @Override
    public boolean removeHandler(ConfigurationsHandler handler) {
        return handlers.remove(handler);
    }

    @Override
    public List<ConfigurationsHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }
}
