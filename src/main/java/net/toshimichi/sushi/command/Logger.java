package net.toshimichi.sushi.command;

public interface Logger {
    void send(LogLevel level, String message);

    void custom(String message);
}
