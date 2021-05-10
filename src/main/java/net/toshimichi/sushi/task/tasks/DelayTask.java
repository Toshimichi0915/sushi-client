package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.TaskAdapter;

public class DelayTask<I> extends TaskAdapter<I, I> {

    private int delay;

    public DelayTask(int delay) {
        this.delay = delay;
    }

    @Override
    public void tick() throws Exception {
        if (delay-- < 0) stop(getInput());
    }
}
