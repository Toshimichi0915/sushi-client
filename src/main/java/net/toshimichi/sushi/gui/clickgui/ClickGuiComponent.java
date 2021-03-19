package net.toshimichi.sushi.gui.clickgui;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.Anchor;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.modules.Category;
import net.toshimichi.sushi.modules.config.Configuration;
import net.toshimichi.sushi.modules.config.Configurations;
import net.toshimichi.sushi.utils.RenderUtils;

import java.awt.*;


public class ClickGuiComponent extends PanelComponent {

    private static final RenderUtils utils = new RenderUtils();
    private final Configurations configurations;
    private final Configuration<Color> background;

    public ClickGuiComponent(Configurations configurations) {
        super(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Anchor.TOP_LEFT, null, c -> c);
        this.configurations = configurations;
        background = configurations.get("background", Color.class, new Color(0, 100, 255), () -> true, "color");
    }

    @Override
    public void onRender() {
        addCategory:
        for (Category category : Sushi.getProfile().getCategories().getAll()) {
            for (Component component : this) {
                if (!(component instanceof ModuleListComponent)) continue;
                if (((ModuleListComponent) component).getCategory().equals(category)) continue addCategory;
            }
            ModuleListComponent newComponent = new ModuleListComponent(category, configurations, utils);
            newComponent.setWidth(100);
            newComponent.setX(size() * 102);
            add(newComponent);
        }

        super.onRender();
    }
}
