package net.toshimichi.sushi.gui.theme;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.FrameComponent;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.modules.Module;

public interface Theme {

    String getId();

    FrameComponent newFrameComponent(Component component);

    PanelComponent<?> newClickGui(Module caller);

    <T> ConfigComponent<T> newConfigComponent(Configuration<T> conf);
}
