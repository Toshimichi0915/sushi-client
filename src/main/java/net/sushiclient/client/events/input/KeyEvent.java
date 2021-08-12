package net.sushiclient.client.events.input;

import net.sushiclient.client.events.CancellableEvent;

abstract public class KeyEvent extends CancellableEvent {

    private final int keyCode;

    public KeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
