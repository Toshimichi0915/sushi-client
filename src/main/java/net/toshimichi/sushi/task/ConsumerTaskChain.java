package net.toshimichi.sushi.task;

import net.toshimichi.sushi.task.tasks.DelayTask;

public interface ConsumerTaskChain<I> extends TaskChain {
    TaskChain use(ConsumerTaskAdapter<? super I> task);

    <R> ConsumerTaskChain<R> supply(TaskAdapter<? super I, R> task);

    ConsumerTaskChain<I> abortIf(TaskAdapter<? super I, Boolean> task);

    default TaskChain use(ConsumerTask<? super I> task) {
        return use(new FunctionalConsumerTask<>(task));
    }

    default TaskChain reuse(ConsumerRepeatTask<? super I> task) {
        return use(new FunctionalConsumerRepeatTask<>(task));
    }

    default <R> ConsumerTaskChain<R> supply(PipeTask<? super I, R> task) {
        return supply(new FunctionalPipeTask<>(task));
    }

    default ConsumerTaskChain<I> abortIf(PipeTask<? super I, Boolean> task) {
        return abortIf(new FunctionalPipeTask<>(task));
    }

    default ConsumerTaskChain<I> delay(int ticks) {
        return supply(new DelayTask<>(ticks));
    }
}
