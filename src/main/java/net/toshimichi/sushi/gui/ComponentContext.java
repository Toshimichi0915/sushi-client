package net.toshimichi.sushi.gui;

public interface ComponentContext<T extends Component> {
    T getOrigin();

    void close();
}
