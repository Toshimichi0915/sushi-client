package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.TaskAdapter;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class DelayTask<I> extends TaskAdapter<I, I> {

    private final Supplier<Integer> delay;
    private int current;

    public DelayTask(Supplier<Integer> delay) {
        this.delay = delay;
    }

    @Override
    public void start(I input) throws Exception {
        super.start(input);
        current = delay.get();
    }

    @Override
    public void tick() throws Exception {
        if (current-- <= 0) stop(getInput());
    }
}
