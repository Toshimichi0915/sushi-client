package net.toshimichi.sushi.events;

public class CancellableEvent extends BaseEvent implements Cancellable {

    private boolean isCancelled;

    public CancellableEvent() {
        super(EventTiming.PRE);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}
