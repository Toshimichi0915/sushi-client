package net.sushiclient.client.modules;

public interface ModuleHandler {

    default void setVisible(boolean visible) {
    }

    default void setEnabled(boolean enabled) {
    }

    default void setPaused(boolean paused) {
    }

    default void setCategory(Category category) {
    }

    default void setKeybind(Keybind bind) {
    }
}
