package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.Task;
import net.toshimichi.sushi.task.TaskAdapter;

public class LastTask<I> extends TaskAdapter<I, I> {

    private final Task task;

    public LastTask(Task task) {
        this.task = task;
    }

    @Override
    public void tick() throws Exception {
        task.tick();
        stop(getInput());
    }
}
