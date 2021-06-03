package net.toshimichi.sushi.task;

import net.toshimichi.sushi.task.tasks.DelayTask;
import net.toshimichi.sushi.task.tasks.LastTask;

public interface TaskChain<I> {

    <R> TaskChain<R> then(TaskAdapter<? super I, R> task);

    <R> TaskChain<R> fail(TaskAdapter<? super Exception, R> task);

    TaskChain<I> last(TaskAdapter<? super I, I> task);

    TaskChain<I> abortIf(TaskAdapter<? super I, Boolean> task);

    void execute();

    default TaskChain<Object> then(Task task) {
        return then(new FunctionalTask(task));
    }

    default TaskChain<Object> consume(ConsumerTask<I> task) {
        return then(new FunctionalConsumerTask<>(task));
    }

    default <R> TaskChain<R> supply(SupplierTask<R> task) {
        return then(new FunctionalSupplierTask<>(task));
    }

    default <R> TaskChain<R> supply(PipeTask<I, R> task) {
        return then(new FunctionalPipeTask<>(task));
    }

    default TaskChain<Object> fail(ConsumerTask<? super Exception> task) {
        return fail(new FunctionalConsumerTask<>(task));
    }

    default TaskChain<I> last(Task task) {
        return last(new LastTask<>(task));
    }

    default TaskChain<I> abortIf(PipeTask<? super I, Boolean> task) {
        return abortIf(new FunctionalPipeTask<>(task));
    }

    default TaskChain<I> delay(int delay) {
        return then(new DelayTask<>(delay));
    }
}
