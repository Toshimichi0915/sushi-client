package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;

import java.util.List;

class FlowRightLayout implements Layout {

    private final PanelComponent<?> target;

    public FlowRightLayout(PanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        int width = 0;
        for (Component component : getComponents()) {
            component.setOrigin(target);
            component.setX(width);
            component.setY(0);
            component.setHeight(target.getHeight());
            width += component.getHeight();
        }
        target.setWidth(width);
    }

    List<? extends Component> getComponents() {
        return target;
    }
}
