package net.toshimichi.sushi.gui.theme;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.*;
import net.toshimichi.sushi.gui.hud.HudComponent;
import net.toshimichi.sushi.modules.Module;

public interface Theme {

    String getId();

    <T extends Component> FrameComponent<T> newFrameComponent(T component);

    PanelComponent<?> newClickGui(Module caller);

    HudComponent newHudComponent();

    <T> ConfigComponent<T> newConfigComponent(Configuration<T> conf);
}
