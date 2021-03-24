package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;

import java.util.List;

class FlowDownLayout implements Layout {

    private final PanelComponent<?> target;

    FlowDownLayout(PanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        int height = 0;
        for (Component component : getComponents()) {
            component.setOrigin(target);
            component.setX(0);
            component.setY(height);
            component.setWidth(target.getWidth());
            height += component.getHeight();
        }
        target.setHeight(height);
    }

    List<? extends Component> getComponents() {
        return target;
    }
}
