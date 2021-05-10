package net.toshimichi.sushi.task;

abstract public class SupplierTaskAdapter<R> extends TaskAdapter<Void, R> {

    private boolean running;
    private R result;

    public void start() {
        if (running) throw new IllegalStateException("This task has already started");
        this.running = true;
    }

    @Override
    public void stop(R result) {
        if (!running) throw new IllegalStateException("This task has already finished");
        this.running = false;
        this.result = result;
    }

    @Override
    final public void start(Void input) {
        start();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public R getResult() {
        return result;
    }
}
