package net.sushiclient.client.gui;

public interface ComponentContext<T extends Component> {
    T getOrigin();

    boolean isOverlay();

    void close();
}
