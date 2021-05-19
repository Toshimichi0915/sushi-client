package net.toshimichi.sushi.events;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventHandlers {

    private static final Set<EventMap> eventMaps = new CopyOnWriteArraySet<>();

    @SuppressWarnings("unchecked")
    public static <T extends Event> void callEvent(T event) {
        LinkedList<EventAdapter<T>> adapters = new LinkedList<>();
        for (EventMap map : getAllEventMap(event.getClass())) {
            for (EventAdapter<?> adapter : map.adapters) {
                adapters.add((EventAdapter<T>) adapter);
            }
        }
        adapters.sort(Comparator.comparingInt(EventAdapter::getPriority));
        for (EventAdapter<T> adapter : adapters) {
            if (event instanceof Cancellable && ((Cancellable) event).isCancelled() && adapter.isIgnoreCancelled())
                continue;
            adapter.call(event);
        }
    }

    public static void register(Object obj, EventAdapter<?> adapter) {
        EventMap map = getEventMap(adapter.getEventClass());
        if (map == null) {
            map = new EventMap(obj, adapter.getEventClass(), new ArrayList<>());
            eventMaps.add(map);
        }
        map.adapters.add(adapter);
        map.adapters.sort(Comparator.comparingInt(EventAdapter::getPriority));
    }

    public static void register(Object o) {
        for (Method method : o.getClass().getMethods()) {
            try {
                register(o, new MethodEventAdapter(o, method));
            } catch (IllegalArgumentException e) {
                // skip
            }
        }
    }

    public static void unregister(Object o) {
        eventMaps.removeIf(map -> o.equals(map.obj));
    }

    private static EventMap getEventMap(Class<?> eventClass) {
        for (EventMap map : eventMaps) {
            if (eventClass.equals(map.getClass()))
                return map;
        }
        return null;
    }

    private static HashSet<EventMap> getAllEventMap(Class<?> eventClass) {
        HashSet<EventMap> result = new HashSet<>();
        for (EventMap map : eventMaps) {
            if (!map.eventClass.isAssignableFrom(eventClass)) continue;
            result.add(map);
        }
        return result;
    }

    private static class EventMap {
        final Object obj;
        final Class<?> eventClass;
        final List<EventAdapter<?>> adapters;

        EventMap(Object obj, Class<?> eventClass, List<EventAdapter<?>> adapters) {
            this.obj = obj;
            this.eventClass = eventClass;
            this.adapters = new CopyOnWriteArrayList<>(adapters);
        }
    }
}
