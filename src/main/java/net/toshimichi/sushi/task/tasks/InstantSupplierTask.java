package net.toshimichi.sushi.task.tasks;

import net.toshimichi.sushi.task.SupplierTask;
import net.toshimichi.sushi.task.TaskAdapter;

public class InstantSupplierTask<Object, R> extends TaskAdapter<Object, R> {

    private final SupplierTask<R> task;

    public InstantSupplierTask(SupplierTask<R> task) {
        this.task = task;
    }

    @Override
    public void start(Object input) throws Exception {
        super.start(input);
        try {
            R result = task.tick();
            stop(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
