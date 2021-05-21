package net.toshimichi.sushi.events;

public interface Event {
    EventTiming getTiming();

    boolean isAsync();
}
