package net.sushiclient.client.task;

@FunctionalInterface
public interface Task {
    void tick() throws Exception;
}
