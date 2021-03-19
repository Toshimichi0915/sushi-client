package net.toshimichi.sushi.gui;

import java.util.List;

public interface SelectComponent<T> extends ListComponent<T> {
    int getMaxSelect();

    void setMaxSelect(int max);

    List<T> getSelected();
}
