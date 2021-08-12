package net.sushiclient.client.task;

public class FunctionalSupplierTask<R> extends TaskAdapter<Object, R> {

    private final SupplierTask<R> delegate;
    private final boolean nullable;

    public FunctionalSupplierTask(boolean nullable, SupplierTask<R> delegate) {
        this.delegate = delegate;
        this.nullable = nullable;
    }

    @Override
    public void tick() throws Exception {
        R r = delegate.tick();
        if (r != null || nullable) stop(r);
    }
}
