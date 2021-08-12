package net.sushiclient.client.gui;

import net.sushiclient.client.gui.layout.Layout;

public interface FrameComponent<T extends Component> extends SettingComponent<T> {
    Layout getLayout();

    Insets getFrame();
}
