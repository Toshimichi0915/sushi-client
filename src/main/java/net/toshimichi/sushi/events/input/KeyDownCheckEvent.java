package net.toshimichi.sushi.events.input;

import net.minecraft.client.settings.KeyBinding;
import net.toshimichi.sushi.events.CancellableEvent;

public class KeyDownCheckEvent extends CancellableEvent {

    private final KeyBinding keyBinding;
    private final boolean pressed;
    private boolean result;

    public KeyDownCheckEvent(KeyBinding keyBinding, boolean pressed, boolean result) {
        this.keyBinding = keyBinding;
        this.pressed = pressed;
        this.result = result;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
