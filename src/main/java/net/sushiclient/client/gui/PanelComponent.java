package net.sushiclient.client.gui;

import net.sushiclient.client.gui.layout.Layout;

public interface PanelComponent<T extends Component> extends ListComponent<T> {
    T getFocusedComponent();

    void setFocusedComponent(T component);

    T getTopComponent(int x, int y);

    Layout getLayout();

    void setLayout(Layout layout);

    default void add(T component, boolean visible) {
        add(component);
        component.setVisible(visible);
    }
}
