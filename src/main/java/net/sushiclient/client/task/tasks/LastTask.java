package net.sushiclient.client.task.tasks;

import net.sushiclient.client.task.Task;
import net.sushiclient.client.task.TaskAdapter;

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
