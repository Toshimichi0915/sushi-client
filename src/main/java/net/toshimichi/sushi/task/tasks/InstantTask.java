package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.Task;
import net.toshimichi.sushi.task.TaskAdapter;

public class InstantTask<I, Object> extends TaskAdapter<I, Object> {

    private final Task task;

    public InstantTask(Task task) {
        this.task = task;
    }

    @Override
    public void start(I input) throws Exception {
        super.start(input);
        try {
            task.tick();
            stop(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}