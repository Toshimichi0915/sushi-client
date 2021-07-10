package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.utils.render.GuiUtils;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.awt.event.KeyEvent;

abstract public class SimpleTextComponent extends BaseComponent {

    private static final int START_HOLD_DELAY = 500;
    private static final int HOLD_DELAY = 50;
    private final ThemeConstants constants;
    private final boolean canEdit;

    private StringBuilder text;
    private long pressMillis;
    private long holdMillis;
    private int holdKeyCode;
    private char holdKey;

    public SimpleTextComponent(ThemeConstants constants, String text, boolean canEdit) {
        this.constants = constants;
        this.text = new StringBuilder(text);
        this.canEdit = canEdit;
        setHeight(14);
    }

    public boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    private void append(int keyCode, char key) {
        if (keyCode == Keyboard.KEY_BACK && text.length() > 0) {
            text.delete(text.length() - 1, text.length());
            onChange(text.toString());
        } else if (isPrintableChar(key)) {
            text.append(key);
            onChange(text.toString());
        }
    }

    @Override
    public void onRender() {
        if (START_HOLD_DELAY < System.currentTimeMillis() - pressMillis &&
                HOLD_DELAY < System.currentTimeMillis() - holdMillis &&
                holdKey != 0) {
            append(holdKeyCode, holdKey);
            holdMillis = System.currentTimeMillis();
        }
        Color background;
        if (canEdit) background = constants.textBoxBackgroundColor.getValue();
        else background = constants.outlineColor.getValue();
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.outlineColor.getValue());
        GuiUtils.drawRect(getWindowX() + 1, getWindowY() + 1, getWidth() - 2, getHeight() - 2, background);
        GuiUtils.prepareText(text.toString(), constants.font.getValue(), constants.textColor.getValue(), 9, true)
                .draw(getWindowX() + 2, getWindowY() + 2);
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        if (!canEdit) return false;
        holdKeyCode = keyCode;
        holdKey = key;
        pressMillis = System.currentTimeMillis();
        append(holdKeyCode, key);
        return true;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        if (!canEdit) return false;
        holdKey = 0;
        if (keyCode == Keyboard.KEY_ESCAPE) {
            getContext().close();
        }
        return true;
    }

    public void setText(String newValue) {
        text = new StringBuilder(newValue);
    }

    protected void onChange(String text) {
    }
}
