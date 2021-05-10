package net.toshimichi.sushi.task;

public class NullTaskAdapter extends TaskAdapter<Void, Void> {

    @Override
    public void start(Void input) {
        super.start(input);
        stop(null);
    }

    @Override
    public void tick() {
    }
}
