package net.sushiclient.client.config;

public interface ConfigurationsHandler {

    default void get(Configuration<?> conf) {
    }

    default void reset() {
    }
}
