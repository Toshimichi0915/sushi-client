package net.toshimichi.sushi.task;

@FunctionalInterface
public interface Task {
    void tick() throws Exception;
}
