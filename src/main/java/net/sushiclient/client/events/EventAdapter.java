package net.sushiclient.client.events;

public interface EventAdapter<T extends Event> {
    void call(T event);

    int getPriority();

    boolean isIgnoreCancelled();

    Class<T> getEventClass();
}
