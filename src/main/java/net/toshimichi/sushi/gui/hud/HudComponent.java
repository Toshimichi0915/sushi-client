package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;

public class HudComponent extends BasePanelComponent<HudElementComponent> {

    private final RootConfigurations conf;
    private final Module module;

    public HudComponent(RootConfigurations conf, Module module) {
        this.conf = conf;
        this.module = module;
        addVirtual(new HotbarHudElementComponent());
        addElement(CoordinatesComponent::new, "coordinates", "Coordinates");
        addElement(TpsComponent::new, "tps", "TPS");
        addElement(ModuleListComponent::new, "modules", "Modules");
    }

    private void addVirtual(VirtualHudElementComponent component) {
        add(component, true);
    }

    private void addElement(ElementConstructor constructor, String id, String name) {
        ConfigurationCategory category = conf.getCategory(id, name, null);
        HudElementComponent component = constructor.newElement(category, new HudConstants(category), id, name);
        component.addHandler(new ConfigHandler(component, this, category));
        add(component, true);
    }

    public HudElementComponent getHudElementComponent(String id) {
        for (HudElementComponent component : this) {
            if (component.getId().equals(id))
                return component;
        }
        return null;
    }

    @Override
    public void setFocusedComponent(HudElementComponent component) {
        super.setFocusedComponent(component);
        remove(component);
        add(0, component);
    }

    @Override
    public void onRender() {
        for (HudElementComponent component : this) {
            component.setVisible(component.isActive());
        }
        super.onRender();
    }

    @Override
    public void onRelocate() {
        setWidth(GuiUtils.getWidth());
        setHeight(GuiUtils.getHeight());
        super.onRelocate();
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        return false;
    }

    @Override
    public void onClose() {
        module.setEnabled(false);
    }

    @FunctionalInterface
    private interface ElementConstructor {
        BaseHudElementComponent newElement(Configurations configurations, HudConstants constants, String id, String name);
    }
}
