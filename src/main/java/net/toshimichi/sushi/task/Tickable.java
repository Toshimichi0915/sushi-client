package net.toshimichi.sushi.task;

public interface Tickable {
    void tick() throws Exception;

    boolean isRunning();
}
