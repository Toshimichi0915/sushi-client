package net.toshimichi.sushi.task;

public class FunctionalTask extends TaskAdapter<Object, Object> {

    private final Task delegate;

    public FunctionalTask(Task delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tick() throws Exception {
        delegate.tick();
        stop(null);
    }
}
