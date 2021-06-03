package net.toshimichi.sushi.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.GuiRenderEvent;
import net.toshimichi.sushi.utils.player.InputUtils;

class LockGuiScreen extends GuiScreen {

    private final GuiScreen parent;
    private final Runnable onClose;

    public LockGuiScreen(GuiScreen parent, Runnable onClose) {
        this.parent = parent;
        this.onClose = onClose;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        EventHandlers.callEvent(new GuiRenderEvent(EventTiming.PRE));
        super.drawScreen(mouseX, mouseY, partialTicks);
        EventHandlers.callEvent(new GuiRenderEvent(EventTiming.POST));
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
        onClose.run();
    }
}
