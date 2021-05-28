package net.toshimichi.sushi.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigInjector {
    private final Configurations provider;
    private final ArrayList<Configuration<?>> configurations = new ArrayList<>();

    public ConfigInjector(Configurations provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    public void inject(Object obj) {
        LinkedHashMap<Field, Config> configs = new LinkedHashMap<>();
        HashMap<Field, Config> suppliers = new HashMap<>();
        HashMap<Config, Supplier<Boolean>> validators = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Config info = field.getAnnotation(Config.class);
            if (info == null) continue;
            configs.put(field, info);
            if (field.getType().equals(Boolean.class)) {
                suppliers.put(field, info);
            }
        }

        // check "when"
        root:
        for (Map.Entry<Field, Config> entry : configs.entrySet()) {
            String when = entry.getValue().when();
            if (when.isEmpty()) {
                validators.put(entry.getValue(), () -> true);
                continue;
            }
            for (Map.Entry<Field, Config> supplier : suppliers.entrySet()) {
                if (when.equals(supplier.getValue().id())) {
                    validators.put(entry.getValue(), () -> {
                        try {
                            return (Boolean) supplier.getKey().get(obj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return true;
                        }
                    });
                    continue root;
                }
            }
            throw new IllegalArgumentException("Config property \"when\" is not valid: " + when + " for " + entry.getValue().id());
        }

        // generate
        for (Map.Entry<Field, Config> entry : configs.entrySet()) {
            Field field = entry.getKey();
            Config info = entry.getValue();
            String desc = info.desc().isEmpty() ? null : info.desc();
            Configuration<Object> conf;
            try {
                conf = provider.get(info.id(), info.name(), desc, (Class<Object>) field.getType(), field.get(obj), validators.get(info), info.temp(), info.prio());
                field.set(obj, conf.getValue());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            conf.addHandler(new ConfigurationHandler<Object>() {

                private boolean ignoreUpdate;

                @Override
                public void getValue(Object original) {
                    try {
                        ignoreUpdate = true;
                        conf.setValue(field.get(obj));
                        ignoreUpdate = false;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void setValue(Object original) {
                    if (ignoreUpdate) return;
                    try {
                        field.set(obj, original);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void save() {
                    getValue(null);
                }
            });
            configurations.add(conf);
        }
    }

    public Configuration<?> getConfiguration(String id) {
        for (Configuration<?> conf : configurations) {
            if (conf.getId().equals(id)) return conf;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> Configuration<T> getConfiguration(String id, Class<T> tClass) {
        for (Configuration<?> conf : configurations) {
            if (conf.getId().equals(id) && conf.getValueClass().equals(tClass)) return (Configuration<T>) conf;
        }
        return null;
    }

    public Configurations getConfigurations() {
        return provider;
    }
}
