package net.sushiclient.client.events.client;

import net.sushiclient.client.events.CancellableEvent;

public class ExceptionCatchEvent extends CancellableEvent {

    private final Throwable throwable;

    public ExceptionCatchEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
