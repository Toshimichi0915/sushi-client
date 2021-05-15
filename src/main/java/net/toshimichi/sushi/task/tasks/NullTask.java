package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.TaskAdapter;

public class NullTask extends TaskAdapter<Object, Object> {

    @Override
    public void start(Object input) throws Exception {
        super.start(input);
        stop(null);
    }
}
