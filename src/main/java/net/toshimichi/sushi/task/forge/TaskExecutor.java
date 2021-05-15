package net.toshimichi.sushi.task.forge;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.WorldLoadEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.task.TaskChain;
import net.toshimichi.sushi.task.tasks.NullTask;

import java.util.ArrayList;

public class TaskExecutor {

    private final ArrayList<TaskContext> contexts = new ArrayList<>();
    private final ArrayList<TaskAdapter<?, ?>> running;
    private final ArrayList<TaskAdapter<?, Boolean>> abort;

    private TaskExecutor(ArrayList<TaskAdapter<?, ?>> running) {
        this.running = running;
        this.abort = new ArrayList<>();
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

    protected void addAbortHandler(TaskAdapter<?, ?> origin, TaskAdapter<?, Boolean> adapter) {
        addTaskAdapter(origin, adapter);
        abort.add(adapter);
    }

    protected void addExceptionHandler(TaskAdapter<?, ?> origin, TaskAdapter<Exception, ?> handler) {
        getTaskContext(origin, true).addExceptionHandler(handler);
    }

    protected void execute() {
        if (Minecraft.getMinecraft().world == null) return;
        EventHandlers.register(this);
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
        if (!task.isRunning()) {
            if (abort.contains(task) && ((TaskAdapter<?, Boolean>) task).getResult()) {
                running.clear();
                return false;
            }
            if (context != null) {
                for (TaskAdapter<?, ?> child : context.getTaskAdapters()) {
                    ((TaskAdapter<Object, ?>) child).start(task.getResult());
                    running.add(child);
                    updateTask(child, false);
                }
            }
            running.remove(task);
            return true;
        }
        return false;
    }

    private void updateTask(TaskAdapter<?, ?> task, boolean tick) {
        TaskContext context = getTaskContext(task, false);
        try {
            if (!refresh(task) && tick) {
                task.tick();
                refresh(task);
            }
        } catch (Exception ex) {
            running.remove(task);
            if (context == null || context.getExceptionHandlers().isEmpty()) {
                ex.printStackTrace();
                return;
            }
            for (TaskAdapter<Exception, ?> handler : context.getExceptionHandlers()) {
                handler.start(ex);
                running.add(handler);
                updateTask(handler, false);
            }
        }
    }

    @EventHandler(timing = EventTiming.PRE, priority = 1000)
    public void onClientTick(ClientTickEvent e) {
        if (running.isEmpty()) {
            EventHandlers.unregister(this);
            return;
        }

        for (TaskAdapter<?, ?> task : new ArrayList<>(running)) {
            updateTask(task, true);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getClient() != null) return;
        // abort all
        EventHandlers.unregister(this);
    }

    public static TaskChain newTaskChain() {
        NullTask firstTask = new NullTask();
        ArrayList<TaskAdapter<?, ?>> adapters = new ArrayList<>();
        adapters.add(firstTask);
        return new ForgeTaskChain(new TaskExecutor(adapters), firstTask);
    }
}
