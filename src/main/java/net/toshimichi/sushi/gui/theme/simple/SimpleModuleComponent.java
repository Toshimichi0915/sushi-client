package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;

import java.awt.Color;

public class SimpleModuleComponent extends BaseComponent {

    private final Module module;
    private final ThemeConstants constants;

    public SimpleModuleComponent(Module module, ThemeConstants constants) {
        this.module = module;
        this.constants = constants;
        setHeight(12);
    }

    @Override
    public void onRender() {
        Color color;
        if (module.isEnabled()) color = constants.enabledColor.getValue();
        else color = constants.disabledColor.getValue();
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
        GuiUtils.drawText(getWindowX() + 10, getWindowY() + 2, module.getName(), null, constants.textColor.getValue(), 9, false);
    }

    public Module getModule() {
        return module;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        if (type == ClickType.LEFT) {
            module.setEnabled(!module.isEnabled());
        }
    }
}
