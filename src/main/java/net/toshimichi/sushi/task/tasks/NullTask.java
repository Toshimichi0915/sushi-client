package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.TaskAdapter;

public class NullTask extends TaskAdapter<Void, Void> {

    @Override
    public void start(Void input) {
        super.start(input);
        stop(null);
    }
}
