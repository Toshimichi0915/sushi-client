package net.toshimichi.sushi.events;

public class KeyReleaseEvent extends CancellableEvent {
    private final int keyCode;

    public KeyReleaseEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
