package net.sushiclient.client.task;

public interface Tickable {
    default void tick() throws Exception {
    }
}
