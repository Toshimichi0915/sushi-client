package net.toshimichi.sushi.task;

public class FunctionalConsumerTask<I> extends TaskAdapter<I, Object> {

    private final ConsumerTask<I> delegate;

    public FunctionalConsumerTask(ConsumerTask<I> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tick() throws Exception {
        delegate.tick(getInput());
        stop(null);
    }
}
