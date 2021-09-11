package net.sushiclient.client.modules;

public interface ModulesHandler {
    default void addModule(Module module) {
    }

    default void removeModule(Module module) {
    }

    default void save() {
    }

    default void load() {
    }

    default void enable() {
    }

    default void disable() {
    }
}
