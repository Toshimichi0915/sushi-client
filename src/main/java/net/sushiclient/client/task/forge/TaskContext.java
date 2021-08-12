package net.sushiclient.client.task.forge;

import net.sushiclient.client.task.TaskAdapter;

class TaskContext {
    private final TaskAdapter<?, ?> origin;
    private TaskAdapter<?, ?> next;
    private TaskAdapter<? super Exception, ?> fail;

    public TaskContext(TaskAdapter<?, ?> origin) {
        this.origin = origin;
    }

    public void next(TaskAdapter<?, ?> adapter) {
        next = adapter;
    }

    public void fail(TaskAdapter<? super Exception, ?> adapter) {
        fail = adapter;
    }

    public TaskAdapter<?, ?> origin() {
        return origin;
    }

    public TaskAdapter<?, ?> next() {
        return next;
    }

    public TaskAdapter<? super Exception, ?> fail() {
        return fail;
    }
}
