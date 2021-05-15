package net.toshimichi.sushi.task.forge;

import net.toshimichi.sushi.task.TaskAdapter;

import java.util.ArrayList;

class TaskContext {
    private final TaskAdapter<?, ?> origin;
    private final ArrayList<TaskAdapter<?, ?>> taskAdapters = new ArrayList<>();
    private final ArrayList<TaskAdapter<? super Exception, ?>> exceptionHandlers = new ArrayList<>();

    public TaskContext(TaskAdapter<?, ?> origin) {
        this.origin = origin;
    }

    public void addTaskAdapter(TaskAdapter<?, ?> taskAdapter) {
        taskAdapters.add(taskAdapter);
    }

    public void addExceptionHandler(TaskAdapter<? super Exception, ?> handler) {
        exceptionHandlers.add(handler);
    }

    public TaskAdapter<?, ?> getOrigin() {
        return origin;
    }

    public ArrayList<TaskAdapter<?, ?>> getTaskAdapters() {
        return taskAdapters;
    }

    public ArrayList<TaskAdapter<? super Exception, ?>> getExceptionHandlers() {
        return exceptionHandlers;
    }
}
