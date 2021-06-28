package net.toshimichi.sushi.task.forge;

import net.toshimichi.sushi.task.TaskAdapter;

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
