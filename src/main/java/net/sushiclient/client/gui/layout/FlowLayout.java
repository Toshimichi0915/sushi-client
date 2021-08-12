package net.sushiclient.client.gui.layout;

import net.sushiclient.client.gui.PanelComponent;

public class FlowLayout implements Layout {

    private final PanelComponent<?> target;
    private final Layout layout;

    public FlowLayout(PanelComponent<?> target, FlowDirection direction) {
        this.target = target;
        this.layout = direction.getFactory().apply(target);
    }

    @Override
    public void relocate() {
        layout.relocate();
    }
}
