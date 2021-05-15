package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.PipeTask;
import net.toshimichi.sushi.task.TaskAdapter;

public class InstantPipeTask<I, R> extends TaskAdapter<I, R> {

    private final PipeTask<I, R> task;

    public InstantPipeTask(PipeTask<I, R> task) {
        this.task = task;
    }

    @Override
    public void start(I input) throws Exception {
        super.start(input);
        try {
            R result = task.tick(input);
            stop(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
