package net.toshimichi.sushi.gui;

public class Anchor {

    public static final Anchor TOP_LEFT = new Anchor(0, 0);
    public static final Anchor TOP_CENTER = new Anchor(0.5, 0);
    public static final Anchor TOP_RIGHT = new Anchor(1, 0);
    public static final Anchor CENTER_LEFT = new Anchor(0, 0.5);
    public static final Anchor CENTER = new Anchor(0.5, 0.5);
    public static final Anchor CENTER_RIGHT = new Anchor(1, 0);
    public static final Anchor BOTTOM_LEFT = new Anchor(0, 1);
    public static final Anchor BOTTOM_CENTER = new Anchor(0.5, 1);
    public static final Anchor BOTTOM_RIGHT = new Anchor(1, 1);

    public static Anchor[] values() {
        return new Anchor[]{TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT};
    }

    private final double x;
    private final double y;

    public Anchor(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Anchor getOpposite() {
        return new Anchor(1 - x, 1 - y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Anchor anchor = (Anchor) o;

        if (Double.compare(anchor.x, x) != 0) return false;
        return Double.compare(anchor.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
