package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.Insets;
import net.toshimichi.sushi.gui.PanelComponent;

import java.util.List;

class FlowRightLayout implements Layout {

    private final PanelComponent<?> target;

    FlowRightLayout(PanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        double width = 0;
        double marginRight = 0;
        for (Component component : getComponents()) {
            if (!component.isVisible()) continue;
            Insets margin = component.getMargin();
            double marginLeft = Math.max(marginRight, margin.getLeft());
            component.setParent(target);
            component.setX(width + marginLeft);
            component.setY(margin.getTop());
            component.setHeight(target.getHeight() - margin.getTop() - margin.getBottom());
            component.onRelocate();
            width += component.getHeight() + marginLeft;
            marginRight = margin.getRight();
        }
        target.setWidth(width + marginRight);
    }

    List<? extends Component> getComponents() {
        return target;
    }
}
