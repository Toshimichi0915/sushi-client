package net.toshimichi.sushi.task;

public class FunctionalConsumerTask<I> extends ConsumerTaskAdapter<I> {

    private final ConsumerTask<I> delegate;

    public FunctionalConsumerTask(ConsumerTask<I> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tick() throws Exception {
        delegate.tick(getItem());
        stop(null);
    }
}
