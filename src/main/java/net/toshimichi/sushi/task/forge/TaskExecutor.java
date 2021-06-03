package net.toshimichi.sushi.task.forge;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.WorldLoadEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.task.Task;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.task.TaskChain;
import net.toshimichi.sushi.task.tasks.NullTask;

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

    private TaskContext getTaskContext(TaskAdapter<?, ?> origin, boolean create) {
        for (TaskContext context : contexts) {
            if (context.getOrigin().equals(origin)) return context;
        }
        TaskContext context = null;
        if (create) {
            context = new TaskContext(origin);
            contexts.add(context);
        }
        return context;
    }

    protected void addTaskAdapter(TaskAdapter<?, ?> origin, TaskAdapter<?, ?> adapter) {
        getTaskContext(origin, true).addTaskAdapter(adapter);
    }

    protected void addExceptionHandler(TaskAdapter<?, ?> origin, TaskAdapter<? super Exception, ?> handler) {
        getTaskContext(origin, true).addExceptionHandler(handler);
    }

    protected void addAbortHandler(TaskAdapter<?, ?> origin, TaskAdapter<?, Boolean> adapter) {
        addTaskAdapter(origin, adapter);
        abortTasks.add(adapter);
    }

    protected void addLastTaskAdapter(TaskAdapter<?, ?> adapter) {
        lastTasks.add(adapter);
    }

    protected void execute() {
        if (Minecraft.getMinecraft().world == null) return;
        EventHandlers.register(this);
        onClientTick(null);
    }

    private <I, R> void executeTask(TaskAdapter<I, R> task, Task exec) {
        TaskContext context = getTaskContext(task, false);
        try {
            exec.tick();
        } catch (Exception e) {
            running.remove(task);
            if (context == null || context.getExceptionHandlers().isEmpty()) {
                e.printStackTrace();
                return;
            }
            for (TaskAdapter<? super Exception, ?> handler : context.getExceptionHandlers()) {
                executeTask(handler, () -> handler.start(e));
                running.add(handler);
            }
            if (running.isEmpty()) startLastTasks();
            updateTask();
        }
    }

    private void startLastTasks() {
        for (TaskAdapter<?, ?> last : lastTasks) {
            executeTask(last, () -> last.start(null));
            running.add(last);
        }
        lastTasks.clear();
        updateTask();
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
            for (TaskAdapter<?, ?> child : context.getTaskAdapters()) {
                TaskAdapter<Object, ?> consumer = (TaskAdapter<Object, ?>) child;
                executeTask(consumer, () -> consumer.start(task.getResult()));
                running.add(child);
            }
            updateTask();
        }
        if (running.isEmpty()) startLastTasks();
        return false;
    }

    private void updateTask() {
        if (running.isEmpty()) return;
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
        if (running.isEmpty()) {
            EventHandlers.unregister(this);
            return;
        }

        updateTask();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getClient() != null) return;
        // abort all
        EventHandlers.unregister(this);
    }

    public static TaskChain<Object> newTaskChain() {
        NullTask firstTask = new NullTask();
        ArrayList<TaskAdapter<?, ?>> adapters = new ArrayList<>();
        adapters.add(firstTask);
        return new ForgeTaskChain<>(new TaskExecutor(adapters), firstTask);
    }
}
