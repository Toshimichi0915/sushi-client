package net.sushiclient.client.utils.render.account;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;

import java.awt.Color;

public class AccountEmptyEntry implements GuiListExtended.IGuiListEntry {

    private final Minecraft mc;
    private final FontRenderer fr;

    public AccountEmptyEntry(Minecraft mc) {
        this.mc = mc;
        this.fr = mc.fontRenderer;
    }

    @Override
    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {

    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        fr.drawString("Empty", x + (listWidth - fr.getStringWidth("Empty")) / 2, y + slotHeight / 2, Color.WHITE.getRGB());
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

    }
}
