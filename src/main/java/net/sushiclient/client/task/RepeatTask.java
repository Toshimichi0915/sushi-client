package net.sushiclient.client.task;

@FunctionalInterface
public interface RepeatTask {
    boolean tick() throws Exception;
}
