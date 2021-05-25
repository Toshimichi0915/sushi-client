package net.toshimichi.sushi.gui.theme;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.FrameComponent;
import net.toshimichi.sushi.modules.Module;

public interface Theme {

    String getId();

    Component newClickGui(Module caller);

    Component newConfigCategoryComponent(Configurations configurations);

    <T extends Component> FrameComponent<T> newFrameComponent(T component);

    <T> ConfigComponent<T> newConfigComponent(Configuration<T> conf);
}
