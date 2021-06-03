package net.toshimichi.sushi.task;

public interface Tickable {
    default void tick() throws Exception {
    }
}
