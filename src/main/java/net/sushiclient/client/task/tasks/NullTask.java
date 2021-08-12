package net.sushiclient.client.task.tasks;

import net.sushiclient.client.task.TaskAdapter;

public class NullTask extends TaskAdapter<Object, Object> {

    @Override
    public void start(Object input) throws Exception {
        super.start(input);
        stop(null);
    }
}
