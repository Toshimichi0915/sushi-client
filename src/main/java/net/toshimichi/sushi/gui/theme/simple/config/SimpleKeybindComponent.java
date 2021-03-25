package net.toshimichi.sushi.gui.theme.simple.config;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.events.input.ClickType;
import net.toshimichi.sushi.gui.MouseStatus;
import net.toshimichi.sushi.gui.base.BaseConfigComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.modules.ActivationType;
import net.toshimichi.sushi.modules.Keybind;
import net.toshimichi.sushi.utils.GuiUtils;
import org.lwjgl.input.Keyboard;

public class SimpleKeybindComponent extends BaseConfigComponent<Keybind> {

    private final ThemeConstants constants;
    private final Configuration<Keybind> conf;
    private final IntArrayList heldKeys = new IntArrayList();
    private boolean listening;

    public SimpleKeybindComponent(ThemeConstants constants, Configuration<Keybind> configuration) {
        super(configuration);
        this.constants = constants;
        this.conf = configuration;
        setHeight(14);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.backgroundColor.getValue());
        StringBuilder builder = new StringBuilder();
        int[] keyCodes = conf.getValue().getKeys();
        if (listening) {
            builder.append("Input keys...");
        } else if (keyCodes.length > 0) {
            builder.append("Keybind: ");
            for (int i = 0; i < keyCodes.length - 1; i++) {
                builder.append(Keyboard.getKeyName(keyCodes[i]));
                builder.append(" + ");
            }
            builder.append(Keyboard.getKeyName(keyCodes[keyCodes.length - 1]));
        } else {
            builder.append("Keybind: NONE");
        }
        GuiUtils.prepareText(builder.toString(), constants.font.getValue(), constants.textColor.getValue(), 10, false)
                .draw(getWindowX() + 1, getWindowY() + 2);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        listening = true;
        heldKeys.clear();
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END) return;
        listening = true;
        heldKeys.clear();
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        heldKeys.add(keyCode);
        return true;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        if (!listening) return false;
        if (keyCode == Keyboard.KEY_ESCAPE) {
            conf.setValue(new Keybind(ActivationType.TOGGLE, 0));
        } else {
            conf.setValue(new Keybind(ActivationType.TOGGLE, heldKeys.toIntArray()));
        }
        listening = false;
        return true;
    }
}
