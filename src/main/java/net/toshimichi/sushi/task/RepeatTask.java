package net.toshimichi.sushi.task;

@FunctionalInterface
public interface RepeatTask {
    boolean tick() throws Exception;
}
