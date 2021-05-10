package net.toshimichi.sushi.task;

import net.toshimichi.sushi.task.tasks.DelayTask;

public interface ConsumerTaskChain<I> extends TaskChain {
    TaskChain use(ConsumerTaskAdapter<I> task);

    <R> ConsumerTaskChain<R> supply(TaskAdapter<I, R> task);

    default TaskChain use(ConsumerTask<I> task) {
        return use(new FunctionalConsumerTask<>(task));
    }

    default TaskChain reuse(ConsumerRepeatTask<I> task) {
        return use(new FunctionalConsumerRepeatTask<>(task));
    }

    default <R> ConsumerTaskChain<R> supply(PipeTask<I, R> task) {
        return supply(new FunctionalPipeTask<>(task));
    }

    default ConsumerTaskChain<I> delay(int ticks) {
        return supply(new DelayTask<>(ticks));
    }
}
