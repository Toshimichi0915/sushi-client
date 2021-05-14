package net.toshimichi.sushi.task;

public class FunctionalConsumerRepeatTask<I> extends ConsumerTaskAdapter<I> {

    private final ConsumerRepeatTask<I> delegate;

    public FunctionalConsumerRepeatTask(ConsumerRepeatTask<I> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tick() throws Exception {
        if (delegate.tick(getInput())) stop(null);
    }
}
