package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Insets;
import net.toshimichi.sushi.gui.PanelComponent;

import java.util.List;

class FlowDownLayout implements Layout {

    private final PanelComponent<?> target;

    FlowDownLayout(PanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        double height = 0;
        double marginBottom = 0;
        for (Component component : getComponents()) {
            if (!component.isVisible()) continue;
            Insets margin = component.getMargin();
            double marginTop = Math.max(marginBottom, margin.getTop());
            component.setParent(target);
            component.setX(margin.getLeft());
            component.setY(height + marginTop);
            component.setWidth(target.getWidth() - margin.getLeft() - margin.getRight());
            component.onRelocate();
            height += component.getHeight() + marginTop;
            marginBottom = margin.getBottom();
        }
        target.setHeight(height + marginBottom);
    }

    List<? extends Component> getComponents() {
        return target;
    }
}
