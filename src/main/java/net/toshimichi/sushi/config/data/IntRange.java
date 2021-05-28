package net.toshimichi.sushi.config.data;

public class IntRange {
    private int current;
    private int top;
    private int bottom;
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
