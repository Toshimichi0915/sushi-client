package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.ConsumerTask;
import net.toshimichi.sushi.task.TaskAdapter;

public class InstantConsumerTask<I, Object> extends TaskAdapter<I, Object> {

    private final ConsumerTask<I> task;

    public InstantConsumerTask(ConsumerTask<I> task) {
        this.task = task;
    }

    @Override
    public void start(I input) throws Exception {
        super.start(input);
        try {
            task.tick(input);
            stop(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
