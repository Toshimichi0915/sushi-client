package net.sushiclient.client.config;

public interface ConfigurationHandler<T> {
    default void getValue(T original) {
    }

    default void setValue(T original) {
    }

    default void save() {
    }
}
