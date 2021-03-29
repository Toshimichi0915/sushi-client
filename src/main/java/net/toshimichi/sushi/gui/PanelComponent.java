package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.gui.layout.Layout;

public interface PanelComponent<T extends Component> extends ListComponent<T> {
    T getFocusedComponent();

    void setFocusedComponent(T component);

    Layout getLayout();

    void setLayout(Layout layout);
}
