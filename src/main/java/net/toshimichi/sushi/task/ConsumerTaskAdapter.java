package net.toshimichi.sushi.task;

abstract public class ConsumerTaskAdapter<I> extends TaskAdapter<I, Void> {

    private boolean running;
    private I input;

    @Override
    public void start(I item) {
        if (running) throw new IllegalStateException("This task has already started");
        this.running = true;
        this.input = item;
    }

    @Override
    final public void stop(Void result) {
        if (!running) throw new IllegalStateException("This task has already finished");
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public I getInput() {
        return input;
    }
}
