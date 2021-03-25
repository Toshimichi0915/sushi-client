package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.data.KeyCode;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.GuiUtils;
import org.lwjgl.input.Keyboard;

public class SimpleKeyCodeComponent extends BaseConfigComponent<KeyCode> {

    private final ThemeConstants constants;
    private final Configuration<KeyCode> conf;
    private boolean listening;

    public SimpleKeyCodeComponent(ThemeConstants constants, Configuration<KeyCode> configuration) {
        super(configuration);
        this.constants = constants;
        this.conf = configuration;
        setHeight(14);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.backgroundColor.getValue());
        String text = listening ? "Input key..." : "Keybind: " + Keyboard.getKeyName(conf.getValue().getKeyCode());
        GuiUtils.prepareText(text, constants.font.getValue(), constants.textColor.getValue(), 10, false)
                .draw(getWindowX() + 1, getWindowY() + 2);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        listening = true;
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END) return;
        listening = true;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        if (!listening) return false;
        if (keyCode == Keyboard.KEY_ESCAPE) {
            conf.setValue(new KeyCode(0));
        } else {
            conf.setValue(new KeyCode(keyCode));
        }
        listening = false;
        return true;
    }
}
