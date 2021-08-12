package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.Color;

public class SimpleModuleToggleComponent extends SimpleToggleComponent<Module> {

    private final ThemeConstants constants;
    private final Module module;
    private final SmoothCollapseComponent<?> component;

    public SimpleModuleToggleComponent(ThemeConstants constants, Module module, SmoothCollapseComponent<?> component) {
        super(constants, module.isEnabled());
        this.constants = constants;
        this.module = module;
        this.component = component;
        setHeight(14);
    }

    @Override
    protected void onChange(boolean newValue) {
        module.setEnabled(newValue);
    }

    @Override
    public void onRender() {
        setToggled(module.isEnabled());
        super.onRender();
        GuiUtils.prepareText(module.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, true)
                .draw(getWindowX() + 5, getWindowY() + 1);
        double x = getWindowX() + getWidth() - 10;
        double y = getWindowY() + getHeight() / 2;
        double midY = getWindowY() + getHeight() / 2 + (component.getProgress() - 0.5) * getHeight() * 1 / 3;
        GuiUtils.drawLine(x, y, x + 3, midY, Color.WHITE, 2);
        GuiUtils.drawLine(x + 3, midY, x + 6, y, Color.WHITE, 2);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        super.onClick(x, y, type);
        if (type != ClickType.RIGHT) return;
        component.setCollapsed(!component.isCollapsed());
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        super.onHold(fromX, fromY, toX, toY, type, status);
        if (status != MouseStatus.END || type != ClickType.RIGHT) return;
        component.setCollapsed(!component.isCollapsed());
    }

    @Override
    public Module getValue() {
        return module;
    }
}
