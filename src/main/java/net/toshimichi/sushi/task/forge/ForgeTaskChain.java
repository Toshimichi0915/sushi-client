package net.toshimichi.sushi.task.forge;

import net.toshimichi.sushi.task.*;

class ForgeTaskChain implements TaskChain {

    private final TaskExecutor taskExecutor;
    private final TaskAdapter<?, ?> parent;

    ForgeTaskChain(TaskExecutor taskExecutor, TaskAdapter<?, ?> parent) {
        this.taskExecutor = taskExecutor;
        this.parent = parent;
    }

    @Override
    public TaskChain then(TaskAdapter<Void, Void> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeTaskChain(getTaskExecutor(), task);
    }

    @Override
    public <I> ConsumerTaskChain<I> supply(SupplierTaskAdapter<I> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeConsumerTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public TaskChain fail(ConsumerTaskAdapter<Exception> task) {
        getTaskExecutor().addExceptionHandler(getParent(), task);
        return new ForgeTaskChain(getTaskExecutor(), task);
    }

    @Override
    public <I> ConsumerTaskChain<I> fail(TaskAdapter<Exception, I> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeConsumerTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public TaskChain abort(TaskAdapter<Void, Boolean> task) {
        getTaskExecutor().addAbortHandler(getParent(), task);
        return new ForgeTaskChain(getTaskExecutor(), task);
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
