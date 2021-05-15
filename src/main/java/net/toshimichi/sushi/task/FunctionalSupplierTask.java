package net.toshimichi.sushi.task;

public class FunctionalSupplierTask<R> extends TaskAdapter<Object, R> {

    private final SupplierTask<R> delegate;

    public FunctionalSupplierTask(SupplierTask<R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tick() throws Exception {
        R r = delegate.tick();
        if (r != null) stop(r);
    }
}
