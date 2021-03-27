package net.toshimichi.sushi.command;

public interface MessageHandler {
    void send(String message, LogLevel level);
}
