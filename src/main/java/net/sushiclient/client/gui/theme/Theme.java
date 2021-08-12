package net.sushiclient.client.gui.theme;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.FrameComponent;
import net.sushiclient.client.modules.Module;

public interface Theme {

    String getId();

    Component newClickGui(Module caller);

    Component newConfigCategoryComponent(Configurations configurations);

    <T extends Component> FrameComponent<T> newFrameComponent(T component);

    <T> ConfigComponent<T> newConfigComponent(Configuration<T> conf);
}
