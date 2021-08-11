package net.toshimichi.sushi.utils;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.data.IntRange;

import java.util.function.Supplier;

public class UpdateTimer {

    private final boolean real;
    private final Supplier<Integer> supplier;
    private long last;

    public UpdateTimer(boolean real, int time) {
        this(real, () -> time);
    }

    public UpdateTimer(boolean real, Configuration<IntRange> conf) {
        this(real, () -> conf.getValue().getCurrent());
    }

    public UpdateTimer(boolean real, Supplier<Integer> supplier) {
        this.real = real;
        this.supplier = supplier;
    }

    public synchronized boolean peek() {
        int req = supplier.get();
        if (real) {
            long now = System.currentTimeMillis();
            return req <= now - last;
        } else {
            int now = TickUtils.current();
            return req <= now - last;
        }
    }

    public synchronized boolean update() {
        int req = supplier.get();
        if (real) {
            long now = System.currentTimeMillis();
            if (req <= now - last) {
                last = now;
                return true;
            } else {
                return false;
            }
        } else {
            int now = TickUtils.current();
            if (req <= now - last) {
                last = now;
                return true;
            } else {
                return false;
            }
        }
    }
}
