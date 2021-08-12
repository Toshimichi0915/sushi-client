package net.sushiclient.client.gui.layout;

import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.base.BasePanelComponent;

public class EmptyLayout implements Layout {

    private final BasePanelComponent<?> target;

    public EmptyLayout(BasePanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        for (Component child : target) {
            if (target.isVisible()) {
                child.onRelocate();
                child.setParent(target);
            }
        }
    }
}
