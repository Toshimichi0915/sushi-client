package net.toshimichi.sushi.task.forge;

import net.toshimichi.sushi.task.ConsumerTaskAdapter;
import net.toshimichi.sushi.task.ConsumerTaskChain;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.task.TaskChain;

class ForgeConsumerTaskChain<I> extends ForgeTaskChain implements ConsumerTaskChain<I> {

    ForgeConsumerTaskChain(TaskExecutor taskExecutor, TaskAdapter<?, ?> parent) {
        super(taskExecutor, parent);
    }

    @Override
    public TaskChain use(ConsumerTaskAdapter<I> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeTaskChain(getTaskExecutor(), task);
    }

    @Override
    public <R> ConsumerTaskChain<R> supply(TaskAdapter<I, R> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeConsumerTaskChain<>(getTaskExecutor(), task);
    }
}
