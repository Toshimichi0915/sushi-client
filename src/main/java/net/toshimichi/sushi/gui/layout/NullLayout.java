package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;

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
