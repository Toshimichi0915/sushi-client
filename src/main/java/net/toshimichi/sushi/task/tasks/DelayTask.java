package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.TaskAdapter;

import java.util.function.IntSupplier;

public class DelayTask<I> extends TaskAdapter<I, I> {

    private final IntSupplier delay;
    private int current;

    public DelayTask(IntSupplier delay) {
        this.delay = delay;
    }

    @Override
    public void start(I input) throws Exception {
        super.start(input);
        current = delay.getAsInt();
    }

    @Override
    public void tick() throws Exception {
        if (current-- <= 0) stop(getInput());
    }
}
