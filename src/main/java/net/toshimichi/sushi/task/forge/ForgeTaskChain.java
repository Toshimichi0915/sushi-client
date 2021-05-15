package net.toshimichi.sushi.task.forge;

import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.task.TaskChain;

class ForgeTaskChain<I> implements TaskChain<I> {

    private final TaskExecutor taskExecutor;
    private final TaskAdapter<?, ?> parent;

    ForgeTaskChain(TaskExecutor taskExecutor, TaskAdapter<?, ?> parent) {
        this.taskExecutor = taskExecutor;
        this.parent = parent;
    }

    @Override
    public <R> TaskChain<R> then(TaskAdapter<? super I, R> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public <R> TaskChain<R> fail(TaskAdapter<? super Exception, R> task) {
        getTaskExecutor().addExceptionHandler(getParent(), task);
        return new ForgeTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public TaskChain<I> abortIf(TaskAdapter<? super I, Boolean> task) {
        getTaskExecutor().addAbortHandler(getParent(), task);
        return new ForgeTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public void execute() {
        getTaskExecutor().execute();
    }

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public TaskAdapter<?, ?> getParent() {
        return parent;
    }
}
