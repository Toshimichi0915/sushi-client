package net.toshimichi.sushi.modules;

public class Keybind {
    private ActivationType activationType;
    private int[] keys;

    public Keybind() {
    }

    public Keybind(ActivationType activationType, int... keyCode) {
        this.activationType = activationType;
        this.keys = keyCode;
    }

    public int[] getKeys() {
        return keys;
    }

    public ActivationType getActivationType() {
        return activationType;
    }
}
