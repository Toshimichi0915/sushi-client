package net.sushiclient.client.config.data;

import com.google.gson.annotations.SerializedName;

public class IntRange {
    @SerializedName("current")
    private int current;
    @SerializedName("top")
    private int top;
    @SerializedName("bottom")
    private int bottom;
    @SerializedName("step")
    private int step;

    public IntRange() {
    }

    public IntRange(int current, int top, int bottom, int step) {
        this.current = current;
        this.top = top;
        this.bottom = bottom;
        this.step = step;
    }

    public int getCurrent() {
        return current;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getStep() {
        return step;
    }

    public IntRange setCurrent(int current) {
        return new IntRange(current, top, bottom, step);
    }
}
