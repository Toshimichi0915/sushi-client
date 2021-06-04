package net.toshimichi.sushi.task;

public class FunctionalSupplierTask<R> extends TaskAdapter<Object, R> {

    private final SupplierTask<R> delegate;
    private final boolean instant;

    public FunctionalSupplierTask(boolean instant, SupplierTask<R> delegate) {
        this.delegate = delegate;
        this.instant = instant;
    }

    @Override
    public void tick() throws Exception {
        R r = delegate.tick();
        if (r != null || instant) stop(r);
    }
}
