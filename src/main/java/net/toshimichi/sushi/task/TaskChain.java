package net.toshimichi.sushi.task;

public interface TaskChain<I> {

    <R> TaskChain<R> then(boolean instant, TaskAdapter<? super I, R> task);

    <R> TaskChain<R> fail(boolean instant, TaskAdapter<? super Exception, R> task);

    TaskChain<I> abortIf(boolean instant, TaskAdapter<? super I, Boolean> task);

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

    default TaskChain<Object> then(boolean instant, Task task) {
        return then(instant, new FunctionalTask(task));
    }

    default TaskChain<Object> consume(boolean instant, ConsumerTask<I> task) {
        return then(instant, new FunctionalConsumerTask<>(task));
    }

    default <R> TaskChain<R> supply(boolean instant, SupplierTask<R> task) {
        return then(instant, new FunctionalSupplierTask<>(task));
    }

    default <R> TaskChain<R> supply(boolean instant, PipeTask<I, R> task) {
        return then(instant, new FunctionalPipeTask<>(task));
    }

    default TaskChain<Object> fail(boolean instant, ConsumerTask<? super Exception> task) {
        return fail(instant, new FunctionalConsumerTask<>(task));
    }

    default TaskChain<I> abortIf(boolean instant, PipeTask<? super I, Boolean> task) {
        return abortIf(instant, new FunctionalPipeTask<>(task));
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
}
