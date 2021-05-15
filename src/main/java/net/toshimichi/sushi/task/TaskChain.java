package net.toshimichi.sushi.task;

import net.toshimichi.sushi.task.tasks.InstantConsumerTask;
import net.toshimichi.sushi.task.tasks.InstantPipeTask;
import net.toshimichi.sushi.task.tasks.InstantSupplierTask;
import net.toshimichi.sushi.task.tasks.InstantTask;

public interface TaskChain<I> {

    <R> TaskChain<R> then(TaskAdapter<? super I, R> task);

    <R> TaskChain<R> fail(TaskAdapter<? super Exception, R> task);

    TaskChain<I> abortIf(TaskAdapter<? super I, Boolean> task);

    void execute();

    default TaskChain<Object> then(boolean instant, Task task) {
        if (instant) return then(new InstantTask<>(task));
        else return then(new FunctionalTask(task));
    }

    default <R> TaskChain<R> supply(boolean instant, SupplierTask<R> task) {
        if (instant) return then(new InstantSupplierTask<>(task));
        else return then(new FunctionalSupplierTask<>(task));
    }

    default TaskChain<Object> consume(boolean instant, ConsumerTask<I> task) {
        if (instant) return then(new InstantConsumerTask<>(task));
        else return then(new FunctionalConsumerTask<>(task));
    }

    default <R> TaskChain<R> pipe(boolean instant, PipeTask<I, R> task) {
        if (instant) return then(new InstantPipeTask<>(task));
        else return then(new FunctionalPipeTask<>(task));
    }

    default TaskChain<Object> then(Task task) {
        return then(false, task);
    }

    default <R> TaskChain<R> supply(SupplierTask<R> task) {
        return supply(false, task);
    }

    default TaskChain<Object> consume(ConsumerTask<I> task) {
        return consume(false, task);
    }

    default <R> TaskChain<R> pipe(PipeTask<I, R> task) {
        return pipe(false, task);
    }

    default TaskChain<Object> fail(ConsumerTask<? super Exception> task) {
        return fail(new FunctionalConsumerTask<>(task));
    }

    default <R> TaskChain<R> fail(PipeTask<? super Exception, R> task) {
        return fail(new FunctionalPipeTask<>(task));
    }

    default TaskChain<I> abortIf(PipeTask<? super I, Boolean> task) {
        return abortIf(new FunctionalPipeTask<>(task));
    }
}
