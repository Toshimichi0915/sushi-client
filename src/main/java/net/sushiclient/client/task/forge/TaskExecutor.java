package net.sushiclient.client.task.forge;

import net.minecraft.client.Minecraft;
import net.sushiclient.annotations.Protect;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.WorldLoadEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.task.Task;
import net.sushiclient.client.task.TaskAdapter;
import net.sushiclient.client.task.TaskChain;
import net.sushiclient.client.task.tasks.NullTask;

import java.util.ArrayList;

public class TaskExecutor {

    private final ArrayList<TaskContext> contexts = new ArrayList<>();
    private final ArrayList<TaskAdapter<?, ?>> running;
    private final ArrayList<TaskAdapter<?, Boolean>> abortTasks;
    private final ArrayList<TaskAdapter<?, ?>> lastTasks;

    private TaskExecutor(ArrayList<TaskAdapter<?, ?>> running) {
        this.running = running;
        this.abortTasks = new ArrayList<>();
        this.lastTasks = new ArrayList<>();
    }

    public static TaskChain<Object> newTaskChain() {
        NullTask firstTask = new NullTask();
        ArrayList<TaskAdapter<?, ?>> adapters = new ArrayList<>();
        adapters.add(firstTask);
        return new ForgeTaskChain<>(new TaskExecutor(adapters), firstTask);
    }

    private TaskContext getTaskContext(TaskAdapter<?, ?> origin, boolean create) {
        for (TaskContext context : contexts) {
            if (context.origin().equals(origin)) return context;
        }
        TaskContext context = null;
        if (create) {
            context = new TaskContext(origin);
            contexts.add(context);
        }
        return context;
    }

    protected void next(TaskAdapter<?, ?> origin, TaskAdapter<?, ?> adapter) {
        getTaskContext(origin, true).next(adapter);
    }

    protected void fail(TaskAdapter<?, ?> origin, TaskAdapter<? super Exception, ?> handler) {
        getTaskContext(origin, true).fail(handler);
    }

    protected void abort(TaskAdapter<?, ?> origin, TaskAdapter<?, Boolean> adapter) {
        next(origin, adapter);
        abortTasks.add(adapter);
    }

    protected void last(TaskAdapter<?, ?> adapter) {
        lastTasks.add(adapter);
    }

    protected void execute() {
        if (Minecraft.getMinecraft().world == null) return;
        EventHandlers.register(this);
        updateTask();
    }

    @SuppressWarnings("unchecked")
    private <I, R> void executeTask(TaskAdapter<I, R> task, Task exec) {
        TaskContext context = getTaskContext(task, false);
        try {
            exec.tick();
        } catch (Exception e) {
            running.remove(task);
            if (context == null || context.fail() == null) {
                e.printStackTrace();
                return;
            }
            if (context.fail() != null) running.add(0, context.fail());
            if (!running.isEmpty()) {
                TaskAdapter<? super Exception, ?> handler = (TaskAdapter<? super Exception, ?>) running.get(0);
                executeTask(handler, () -> handler.start(e));
                updateTask();
            } else {
                startLastTasks();
            }
        }
    }

    private void startLastTasks() {
        running.addAll(0, lastTasks);
        lastTasks.clear();
        if (!running.isEmpty()) {
            TaskAdapter<?, ?> last = running.get(0);
            executeTask(last, () -> last.start(null));
            updateTask();
        }
    }

    /**
     * Refreshes the task.
     *
     * @param task Task to be refreshed.
     * @return {@code true} if {@link TaskAdapter#tick()} should be called, otherwise {@code false}
     */
    @SuppressWarnings("unchecked")
    private boolean refresh(TaskAdapter<?, ?> task) {
        TaskContext context = getTaskContext(task, false);
        if (task.isRunning()) return true;
        if (abortTasks.contains(task) && ((TaskAdapter<?, Boolean>) task).getResult()) {
            running.clear();
            startLastTasks();
            return false;
        }
        running.remove(task);
        if (context != null) {
            if (context.next() != null) running.add(0, context.next());
            if (!running.isEmpty()) {
                TaskAdapter<Object, ?> consumer = (TaskAdapter<Object, ?>) running.get(0);
                executeTask(consumer, () -> consumer.start(task.getResult()));
                updateTask();
            }
        }
        if (running.isEmpty()) startLastTasks();
        return false;
    }

    private void updateTask() {
        if (running.isEmpty()) {
            EventHandlers.unregister(this);
            return;
        }
        TaskAdapter<?, ?> task = running.get(0);
        executeTask(task, () -> {
            if (refresh(task)) {
                task.tick();
                refresh(task);
            }
        });
    }

    @EventHandler(timing = EventTiming.PRE, priority = 1000)
    public void onClientTick(ClientTickEvent e) {
        updateTask();
    }

    @Protect
    @EventHandler(timing = EventTiming.PRE)
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getClient() != null) return;
        // abort all
        EventHandlers.unregister(this);
    }
}
