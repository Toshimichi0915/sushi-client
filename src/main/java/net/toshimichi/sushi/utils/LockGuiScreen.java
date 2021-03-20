package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.toshimichi.sushi.events.input.KeyEvent;
import net.toshimichi.sushi.events.input.MouseEvent;

import java.io.IOException;

class LockGuiScreen extends GuiScreen {

    private final GuiScreen parent;

    public LockGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        KeyEvent event = InputUtils.callKeyEvent();
        if (event != null && !event.isCancelled())
            super.handleKeyboardInput();
    }

    @Override
    public void handleMouseInput() throws IOException {
        MouseEvent event = InputUtils.callMouseEvent();
        if (event != null && !event.isCancelled())
            super.handleMouseInput();
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
