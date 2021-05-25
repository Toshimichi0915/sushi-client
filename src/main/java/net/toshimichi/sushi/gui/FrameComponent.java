package net.toshimichi.sushi.gui;

import net.toshimichi.sushi.gui.layout.Layout;

public interface FrameComponent<T extends Component> extends SettingComponent<T> {
    Layout getLayout();

    Insets getFrame();
}
