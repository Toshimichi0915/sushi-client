package net.sushiclient.client.events.input;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

abstract public class KeyEvent extends BaseEvent {

    private final int keyCode;

    public KeyEvent(int keyCode) {
        super(EventTiming.PRE);
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
