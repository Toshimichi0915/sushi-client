package net.toshimichi.sushi.task.forge;

import net.toshimichi.sushi.task.ConsumerTaskAdapter;
import net.toshimichi.sushi.task.ConsumerTaskChain;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.task.TaskChain;
import net.toshimichi.sushi.task.tasks.RelayTask;

class ForgeConsumerTaskChain<I> extends ForgeTaskChain implements ConsumerTaskChain<I> {

    ForgeConsumerTaskChain(TaskExecutor taskExecutor, TaskAdapter<?, ?> parent) {
        super(taskExecutor, parent);
    }

    @Override
    public TaskChain use(ConsumerTaskAdapter<? super I> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeTaskChain(getTaskExecutor(), task);
    }

    @Override
    public <R> ConsumerTaskChain<R> supply(TaskAdapter<? super I, R> task) {
        getTaskExecutor().addTaskAdapter(getParent(), task);
        return new ForgeConsumerTaskChain<>(getTaskExecutor(), task);
    }

    @Override
    public ConsumerTaskChain<I> abortIf(TaskAdapter<? super I, Boolean> task) {
        getTaskExecutor().addAbortHandler(getParent(), task);
        RelayTask<? super I> relayTask = new RelayTask<>(task);
        getTaskExecutor().addTaskAdapter(task, relayTask);
        return new ForgeConsumerTaskChain<>(getTaskExecutor(), relayTask);
    }
}
