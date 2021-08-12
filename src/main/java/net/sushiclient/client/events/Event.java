package net.sushiclient.client.events;

public interface Event {
    EventTiming getTiming();

    boolean isAsync();
}
