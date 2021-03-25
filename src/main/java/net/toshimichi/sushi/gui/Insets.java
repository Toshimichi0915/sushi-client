package net.toshimichi.sushi.gui;

public class Insets {
    private final int top;
    private final int right;
    private final int bottom;
    private final int left;

    public Insets(int top, int right, int bottom, int left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }
}
