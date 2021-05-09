package net.toshimichi.sushi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import net.toshimichi.sushi.mixin.AccessorMinecraft;
import net.toshimichi.sushi.mixin.AccessorTimer;

import java.util.Stack;

public class TimerUtils {
    private static final Stack<Float> timerStack = new Stack<>();

    private static void setTimer(float speed) {
        Timer timer = ((AccessorMinecraft) Minecraft.getMinecraft()).getTimer();
        ((AccessorTimer) timer).setTickLength(50 / speed);
    }

    public static float getTimer() {
        Timer timer = ((AccessorMinecraft) Minecraft.getMinecraft()).getTimer();
        return 50 / ((AccessorTimer) timer).getTickLength();
    }

    public static void push(float speed) {
        timerStack.push(getTimer());
        setTimer(speed);
    }

    public static void pop() {
        Float tickLength = timerStack.pop();
        if (tickLength == null) tickLength = 50F;
        setTimer(tickLength);
    }
}
