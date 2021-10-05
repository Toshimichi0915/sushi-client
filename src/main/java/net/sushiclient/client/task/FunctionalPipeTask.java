package net.sushiclient.client.task;

public class FunctionalPipeTask<I, R> extends TaskAdapter<I, R> {

    private final PipeTask<I, R> delegate;
    private final boolean nullable;

    public FunctionalPipeTask(boolean nullable, PipeTask<I, R> delegate) {
        this.delegate = delegate;
        this.nullable = nullable;
    }

    @Override
    public void tick() throws Exception {
        R r = delegate.tick(getInput());
        if (r != null || nullable) stop(r);
    }
}
