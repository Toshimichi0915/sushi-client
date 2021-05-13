package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.TaskAdapter;

public class RelayTask<I> extends TaskAdapter<Void, I> {

    private final TaskAdapter<I, ?> parent;

    public RelayTask(TaskAdapter<I, ?> parent) {
        this.parent = parent;
    }

    @Override
    public void start(Void input) {
        super.start(input);
        stop(parent.getInput());
    }
}
