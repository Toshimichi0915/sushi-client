package net.sushiclient.client.config.data;

import com.google.gson.annotations.SerializedName;

public class DoubleRange {
    @SerializedName("current")
    private double current;
    @SerializedName("top")
    private double top;
    @SerializedName("bottom")
    private double bottom;
    @SerializedName("step")
    private double step;
    @SerializedName("digits")
    private int digits;

    public DoubleRange() {
    }

    public DoubleRange(double current, double top, double bottom, double step, int digits) {
        this.current = current;
        this.top = top;
        this.bottom = bottom;
        this.step = step;
        this.digits = digits;
    }

    public double getCurrent() {
        return current;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public double getStep() {
        return step;
    }

    public int getDigits() {
        return digits;
    }

    public DoubleRange setCurrent(double current) {
        return new DoubleRange(current, top, bottom, step, digits);
    }
}
