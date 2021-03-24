package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class FlowUpLayout extends FlowDownLayout {

    private final PanelComponent<?> target;

    FlowUpLayout(PanelComponent<?> target) {
        super(target);
        this.target = target;
    }

    @Override
    List<? extends Component> getComponents() {
        ArrayList<Component> reversed = new ArrayList<>(target);
        Collections.reverse(reversed);
        return reversed;
    }
}
