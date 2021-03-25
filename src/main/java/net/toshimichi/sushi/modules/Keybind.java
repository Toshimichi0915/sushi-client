package net.toshimichi.sushi.modules;

import java.util.Arrays;

public class Keybind {
    private ActivationType activationType;
    private int[] keys;

    public Keybind() {
    }

    public Keybind(ActivationType activationType, int... keyCode) {
        this.activationType = activationType;
        this.keys = keyCode;
        Arrays.sort(keys);
    }

    public int[] getKeys() {
        return keys;
    }

    public ActivationType getActivationType() {
        return activationType;
    }
}
