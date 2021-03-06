package net.toshimichi.sushi.events;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
