package net.toshimichi.sushi.task.forge;

import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.task.tasks.NullTask;
import net.toshimichi.sushi.task.TaskAdapter;
import net.toshimichi.sushi.task.TaskChain;

import java.util.ArrayList;

public class TaskExecutor {

    private final ArrayList<TaskContext> contexts = new ArrayList<>();
    private final ArrayList<TaskAdapter<?, ?>> running;

    private TaskExecutor(ArrayList<TaskAdapter<?, ?>> running) {
        this.running = running;
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

    protected void addExceptionHandler(TaskAdapter<?, ?> origin, TaskAdapter<Exception, ?> handler) {
        getTaskContext(origin, true).addExceptionHandler(handler);
    }

    protected void execute() {
        EventHandlers.register(this);
    }

    @SuppressWarnings("unchecked")
    @EventHandler(timing = EventTiming.PRE, priority = 1000)
    public void onClientTick(ClientTickEvent e) {
        if (running.isEmpty()) {
            EventHandlers.unregister(this);
            return;
        }

        for (TaskAdapter<?, ?> task : new ArrayList<>(running)) {
            TaskContext context = getTaskContext(task, false);
            try {
                if (!task.isRunning()) {
                    if (context != null) {
                        for (TaskAdapter<?, ?> child : context.getTaskAdapters()) {
                            ((TaskAdapter<Object, ?>) child).start(task.getResult());
                            running.add(child);
                        }
                    }
                    running.remove(task);
                    continue;
                }
                task.tick();
            } catch (Exception ex) {
                if (context == null || context.getExceptionHandlers().isEmpty()) {
                    ex.printStackTrace();
                    return;
                }
                for (TaskAdapter<Exception, ?> handler : context.getExceptionHandlers()) {
                    handler.start(ex);
                    running.add(handler);
                }
            }
        }
    }

    public static TaskChain newTaskChain() {
        NullTask firstTask = new NullTask();
        ArrayList<TaskAdapter<?, ?>> adapters = new ArrayList<>();
        adapters.add(firstTask);
        return new ForgeTaskChain(new TaskExecutor(adapters), firstTask);
    }
}
