package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

class LockGuiScreen extends GuiScreen {

    private final GuiScreen parent;

    public LockGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void handleKeyboardInput() {
        InputUtils.callKeyEvent();
    }

    @Override
    public void handleMouseInput() {
        InputUtils.callMouseEvent();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public GuiScreen getParent() {
        return parent;
    }

    public void close() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
    }
}
