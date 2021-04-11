package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.base.BasePanelComponent;

public class EmptyLayout implements Layout {

    private final BasePanelComponent<?> target;

    public EmptyLayout(BasePanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        for (Component component : target) {
            if (target.isVisible())
                component.setParent(target);
        }
    }
}