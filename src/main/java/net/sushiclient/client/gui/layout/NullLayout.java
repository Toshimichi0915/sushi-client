package net.sushiclient.client.gui.layout;

import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.PanelComponent;

public class NullLayout implements Layout {

    private final PanelComponent<?> target;

    public NullLayout(PanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        for (Component child : target) {
            if (child.isVisible())
                child.onRelocate();
        }
    }
}
