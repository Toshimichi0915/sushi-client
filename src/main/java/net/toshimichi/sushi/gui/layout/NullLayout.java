package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.base.BasePanelComponent;

public class NullLayout implements Layout {

    private final BasePanelComponent<?> target;

    public NullLayout(BasePanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        for (Component component : target) {
            component.setParent(target);
        }
    }
}
