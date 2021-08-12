package net.sushiclient.client.task;

public class FunctionalPipeTask<I, R> extends TaskAdapter<I, R> {

    private final PipeTask<I, R> delegate;
    private final boolean instant;

    public FunctionalPipeTask(boolean instant, PipeTask<I, R> delegate) {
        this.delegate = delegate;
        this.instant = instant;
    }

    @Override
    public void tick() throws Exception {
        R r = delegate.tick(getInput());
        if (r != null || instant) stop(r);
    }
}
