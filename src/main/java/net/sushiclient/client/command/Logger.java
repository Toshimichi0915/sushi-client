package net.sushiclient.client.command;

public interface Logger {
    void send(LogLevel level, String message);
}
