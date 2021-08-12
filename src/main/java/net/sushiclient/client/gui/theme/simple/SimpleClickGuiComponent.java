package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.base.BasePanelComponent;
import net.sushiclient.client.gui.layout.EmptyLayout;
import net.sushiclient.client.gui.theme.Theme;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.modules.Category;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.utils.render.GuiUtils;


public class SimpleClickGuiComponent extends BasePanelComponent<SimpleCategoryComponent> {

    private final ThemeConstants constants;
    private final Theme theme;
    private final Configurations configurations;
    private final Module module;

    public SimpleClickGuiComponent(ThemeConstants constants, Theme theme, Configurations configurations, Module module) {
        this.constants = constants;
        this.theme = theme;
        this.configurations = configurations;
        this.module = module;
        setLayout(new EmptyLayout(this));
        GuiUtils.prepareFont(constants.font.getValue(), 9);
        GuiUtils.prepareFont(constants.font.getValue(), 10);
    }

    @Override
    public void setFocusedComponent(SimpleCategoryComponent component) {
        super.setFocusedComponent(component);
        remove(component);
        add(0, component);
    }

    @Override
    public void onScroll(int deltaX, int deltaY) {
        setX(getX() + (double) deltaX / 60);
        setY(getY() + (double) deltaY / 60);
    }

    @Override
    public void onRelocate() {
        setWidth(Double.MAX_VALUE);
        setHeight(Double.MAX_VALUE);

        addCategory:
        for (Category category : Sushi.getProfile().getCategories().getAll()) {
            for (SimpleCategoryComponent component : this) {
                if (component.getCategory().equals(category)) continue addCategory;
            }
            SimpleCategoryComponent newComponent = new SimpleCategoryComponent(constants, theme, configurations, category, size() * 102 + 50, 20);
            add(newComponent, true);
            newComponent.setWidth(100);
        }

        super.onRelocate();
    }

    @Override
    public void onClose() {
        module.setEnabled(false);
    }
}
