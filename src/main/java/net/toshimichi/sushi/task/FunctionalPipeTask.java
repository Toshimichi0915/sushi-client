package net.toshimichi.sushi.task;

public class FunctionalPipeTask<I, R> extends TaskAdapter<I, R> {

    private final PipeTask<I, R> delegate;

    public FunctionalPipeTask(PipeTask<I, R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tick() throws Exception {
        R r = delegate.tick(getInput());
        if (r != null) stop(r);
    }
}
