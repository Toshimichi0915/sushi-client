package net.toshimichi.sushi.task;

abstract public class TaskAdapter<I, R> implements Tickable {
    private boolean running;
    private I input;
    private R result;

    public void start(I input) throws Exception {
        if (running) throw new IllegalStateException("This task has already started");
        this.running = true;
        this.input = input;
    }

    public void stop(R result) {
        if (!running) throw new IllegalStateException("This task has already finished");
        this.running = false;
        this.result = result;
    }

    public boolean isRunning() {
        return running;
    }

    public I getInput() {
        return input;
    }

    public R getResult() {
        return result;
    }
}
