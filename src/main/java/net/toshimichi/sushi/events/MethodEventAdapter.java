package net.toshimichi.sushi.events;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodEventAdapter implements EventAdapter<Event> {

    private final Object obj;
    private final Method method;
    private final Class<Event> eventClass;
    private final int priority;
    private final boolean ignoreCancelled;

    @SuppressWarnings("unchecked")
    public MethodEventAdapter(Object obj, Method method) {
        this.obj = obj;
        this.method = method;
        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length != 1)
            throw new IllegalArgumentException("Only 1 parameter type can exist");
        try {
            eventClass = (Class<Event>) parameters[0];
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Parameter type must implement Event");
        }
        EventHandler handler = method.getAnnotation(EventHandler.class);
        if (handler == null)
            throw new IllegalArgumentException("@EventHandler is missing");
        this.priority = handler.priority();
        this.ignoreCancelled = handler.ignoreCancelled();
    }

    @Override
    public void call(Event event) {
        try {
            method.invoke(obj, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isIgnoreCancelled() {
        return ignoreCancelled;
    }

    @Override
    public Class<Event> getEventClass() {
        return eventClass;
    }


}
