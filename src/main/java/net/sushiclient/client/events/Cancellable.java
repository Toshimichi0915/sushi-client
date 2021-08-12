package net.sushiclient.client.events;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
