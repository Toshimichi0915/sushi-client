package net.toshimichi.sushi.gui.layout;

import net.toshimichi.sushi.gui.PanelComponent;

import java.util.function.Function;

public enum FlowDirection {
    UP(FlowUpLayout::new),
    LEFT(FlowLeftLayout::new),
    RIGHT(FlowRightLayout::new),
    DOWN(FlowDownLayout::new);

    private final Function<PanelComponent<? extends net.toshimichi.sushi.gui.Component>, Layout> factory;

    FlowDirection(Function<PanelComponent<? extends net.toshimichi.sushi.gui.Component>, Layout> factory) {
        this.factory = factory;
    }

    public Function<PanelComponent<? extends net.toshimichi.sushi.gui.Component>, Layout> getFactory() {
        return factory;
    }
}
