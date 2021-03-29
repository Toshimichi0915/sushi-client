package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.PanelComponent;

public class FlowLayout implements Layout {

    private final Layout layout;

    public FlowLayout(PanelComponent<? extends net.toshimichi.sushi.gui.Component> target, FlowDirection direction) {
        layout = direction.getFactory().apply(target);
    }

    @Override
    public void relocate() {
        layout.relocate();
    }
}
