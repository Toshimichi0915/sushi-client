package net.toshimichi.sushi.gui.theme;

import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.FrameComponent;
import net.toshimichi.sushi.gui.PanelComponent;

public interface Theme {

    String getId();

    FrameComponent newFrame(Component component);

    PanelComponent newClickGui();
}
