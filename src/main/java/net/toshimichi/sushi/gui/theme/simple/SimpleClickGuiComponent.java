package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.gui.Anchor;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Category;
import org.lwjgl.opengl.Display;


public class SimpleClickGuiComponent extends PanelComponent {

    private final ThemeConstants constants;

    public SimpleClickGuiComponent(ThemeConstants constants) {
        super(0, 0, Display.getWidth(), Display.getHeight(), Anchor.TOP_LEFT, null, null);
        this.constants = constants;
    }

    @Override
    public void onRender() {
        addCategory:
        for (Category category : Sushi.getProfile().getCategories().getAll()) {
            for (Component component : this) {
                if (!(component instanceof SimpleModuleListComponent)) continue;
                if (((SimpleModuleListComponent) component).getCategory().equals(category)) continue addCategory;
            }
            SimpleModuleListComponent newComponent = new SimpleModuleListComponent(category, constants);
            newComponent.setWidth(100);
            newComponent.setX(size() * 102 + 100);
            newComponent.setY(20);
            newComponent.setVisible(true);
            add(newComponent);
        }

        super.onRender();
    }
}
