package net.toshimichi.sushi.events.client;

import net.toshimichi.sushi.events.CancellableEvent;
import net.toshimichi.sushi.events.EventTiming;

public class ExceptionCatchEvent extends CancellableEvent {

    private final Throwable throwable;

    public ExceptionCatchEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
