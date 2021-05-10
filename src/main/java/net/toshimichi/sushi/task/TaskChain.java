package net.toshimichi.sushi.task;

import net.toshimichi.sushi.task.tasks.DelayTask;

public interface TaskChain {

    TaskChain then(TaskAdapter<Void, Void> task);

    <I> ConsumerTaskChain<I> supply(SupplierTaskAdapter<I> task);

    TaskChain fail(ConsumerTaskAdapter<Exception> task);

    <I> ConsumerTaskChain<I> fail(TaskAdapter<Exception, I> task);

    void execute();

    default TaskChain then(Task task) {
        return then(new FunctionalTask(task));
    }

    default TaskChain repeat(RepeatTask task) {
        return then(new FunctionalRepeatTask(task));
    }

    default <I> ConsumerTaskChain<I> supply(SupplierTask<I> task) {
        return supply(new FunctionalSupplierTask<>(task));
    }

    default TaskChain fail(ConsumerTask<Exception> task) {
        return fail(new FunctionalConsumerTask<>(task));
    }

    default <I> ConsumerTaskChain<I> fail(PipeTask<Exception, I> task) {
        return fail(new FunctionalPipeTask<>(task));
    }

    default TaskChain delay(int delay) {
        return then(new DelayTask<>(delay));
    }
}
