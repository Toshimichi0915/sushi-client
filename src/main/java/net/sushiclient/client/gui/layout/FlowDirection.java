package net.sushiclient.client.gui.layout;

import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.PanelComponent;

import java.util.function.Function;

public enum FlowDirection {
    UP(FlowUpLayout::new),
    LEFT(FlowLeftLayout::new),
    RIGHT(FlowRightLayout::new),
    DOWN(FlowDownLayout::new);

    private final Function<PanelComponent<? extends Component>, Layout> factory;

    FlowDirection(Function<PanelComponent<? extends Component>, Layout> factory) {
        this.factory = factory;
    }

    public Function<PanelComponent<? extends Component>, Layout> getFactory() {
        return factory;
    }
}
