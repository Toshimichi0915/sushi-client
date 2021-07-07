package net.toshimichi.sushi.modules;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Keybind {
    @SerializedName("activation_type")
    private ActivationType activationType;
    @SerializedName("keys")
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
