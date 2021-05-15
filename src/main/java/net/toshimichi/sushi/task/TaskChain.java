package net.toshimichi.sushi.task;

import net.toshimichi.sushi.task.tasks.DelayTask;

public interface TaskChain<I> {

    <R> TaskChain<R> then(boolean delay, TaskAdapter<? super I, R> task);

    <R> TaskChain<R> fail(boolean delay, TaskAdapter<? super Exception, R> task);

    TaskChain<I> abortIf(boolean delay, TaskAdapter<? super I, Boolean> task);

    void execute();

    default <R> TaskChain<R> then(TaskAdapter<? super I, R> task) {
        return then(false, task);
    }

    default <R> TaskChain<R> fail(TaskAdapter<? super Exception, R> task) {
        return fail(false, task);
    }

    default TaskChain<I> abortIf(TaskAdapter<? super I, Boolean> task) {
        return abortIf(false, task);
    }

    default TaskChain<Object> then(boolean delay, Task task) {
        return then(delay, new FunctionalTask(task));
    }

    default TaskChain<Object> consume(boolean delay, ConsumerTask<I> task) {
        return then(delay, new FunctionalConsumerTask<>(task));
    }

    default <R> TaskChain<R> supply(boolean delay, SupplierTask<R> task) {
        return then(delay, new FunctionalSupplierTask<>(task));
    }

    default <R> TaskChain<R> supply(boolean delay, PipeTask<I, R> task) {
        return then(delay, new FunctionalPipeTask<>(task));
    }

    default TaskChain<Object> fail(boolean delay, ConsumerTask<? super Exception> task) {
        return fail(delay, new FunctionalConsumerTask<>(task));
    }

    default TaskChain<I> abortIf(boolean delay, PipeTask<? super I, Boolean> task) {
        return abortIf(delay, new FunctionalPipeTask<>(task));
    }

    default TaskChain<Object> then(Task task) {
        return then(false, task);
    }

    default TaskChain<Object> consume(ConsumerTask<I> task) {
        return consume(false, task);
    }

    default <R> TaskChain<R> supply(SupplierTask<R> task) {
        return supply(false, task);
    }

    default <R> TaskChain<R> supply(PipeTask<I, R> task) {
        return supply(false, task);
    }

    default <R> TaskChain<R> fail(PipeTask<? super Exception, R> task) {
        return fail(false, new FunctionalPipeTask<>(task));
    }

    default TaskChain<I> abortIf(PipeTask<? super I, Boolean> task) {
        return abortIf(false, new FunctionalPipeTask<>(task));
    }

    default TaskChain<I> delay(int delay) {
        return then(new DelayTask<>(delay));
    }
}
