package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;

public class SimpleModuleToggleComponent extends SimpleToggleComponent<Module> {

    private final ThemeConstants constants;
    private final SimpleModuleComponent parent;
    private final Module module;

    public SimpleModuleToggleComponent(ThemeConstants constants, Module module, SimpleModuleComponent parent) {
        super(constants, module.isEnabled());
        this.constants = constants;
        this.module = module;
        this.parent = parent;
        setHeight(16);
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
                .draw(getWindowX() + 12, getWindowY() + 2);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        super.onClick(x, y, type);
        if (type != ClickType.RIGHT) return;
        parent.setCollapsed(!parent.isCollapsed());
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        super.onHold(fromX, fromY, toX, toY, type, status);
        if (status != MouseStatus.END || type != ClickType.RIGHT) return;
        parent.setCollapsed(!parent.isCollapsed());
    }

    @Override
    public Module getValue() {
        return module;
    }
}
