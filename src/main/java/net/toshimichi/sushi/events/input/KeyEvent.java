package net.toshimichi.sushi.events.input;

import net.toshimichi.sushi.events.CancellableEvent;

abstract public class KeyEvent extends CancellableEvent {

    private final int keyCode;

    public KeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
