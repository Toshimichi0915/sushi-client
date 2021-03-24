package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.PanelComponent;

import java.util.function.Function;

public enum FlowDirection {
    UP(FlowUpLayout::new),
    LEFT(FlowLeftLayout::new),
    RIGHT(FlowRightLayout::new),
    DOWN(FlowDownLayout::new);

    private final Function<PanelComponent<?>, Layout> factory;

    FlowDirection(Function<PanelComponent<?>, Layout> factory) {
        this.factory = factory;
    }

    public Function<PanelComponent<?>, Layout> getFactory() {
        return factory;
    }
}
