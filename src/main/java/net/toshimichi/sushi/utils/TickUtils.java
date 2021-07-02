package net.toshimichi.sushi.utils;

public class TickUtils {

    private static int counter;

    public static int current() {
        return counter;
    }

    public static void tick() {
        counter++;
    }
}
