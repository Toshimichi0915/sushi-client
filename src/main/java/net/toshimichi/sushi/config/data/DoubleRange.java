package net.toshimichi.sushi.config.data;

public class DoubleRange {
    private double current;
    private double top;
    private double bottom;
    private double step;
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
