package net.toshimichi.sushi.command;

public interface MessageHandler {
    void send(LogLevel level, String message);

    void custom(String message);
}
