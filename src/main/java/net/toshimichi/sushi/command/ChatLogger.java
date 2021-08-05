package net.toshimichi.sushi.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.util.text.TextFormatting.*;

public class ChatLogger implements Logger {
    @Override
    public void send(LogLevel level, String message) {
        TextFormatting color;
        if (level == LogLevel.WARN) color = YELLOW;
        else if (level == LogLevel.ERROR) color = RED;
        else color = AQUA;
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentString(color + message));
    }

    @Override
    public void custom(String message) {
        Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.CHAT, new TextComponentString(message));
    }
}
