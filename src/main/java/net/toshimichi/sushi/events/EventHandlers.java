package net.toshimichi.sushi.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class EventHandlers {

    private static final HashSet<EventMap> eventMaps = new HashSet<>();

    @SuppressWarnings("unchecked")
    public static <T extends Event> void callEvent(T event) {
        HashSet<EventMap> matchedMaps = getAllEventMap(event.getClass());
        for (EventMap map : matchedMaps) {
            for (EventAdapter<?> adapter : map.adapters) {
                ((EventAdapter<T>) adapter).call(event);
            }
        }
    }

    public static void register(EventAdapter<?> adapter) {
        EventMap map = getEventMap(adapter.getEventClass());
        if (map == null) {
            map = new EventMap(adapter.getEventClass(), new ArrayList<>());
            eventMaps.add(map);
        }
        map.adapters.add(adapter);
        map.adapters.sort(Comparator.comparingInt(EventAdapter::getPriority));
    }

    public static void register(Object o) {
        for (Method method : o.getClass().getMethods()) {
            try {
                register(new MethodEventAdapter(o, method));
            } catch (IllegalArgumentException e) {
                // skip
            }
        }
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
        final Class<?> eventClass;
        final ArrayList<EventAdapter<?>> adapters;

        public EventMap(Class<?> eventClass, ArrayList<EventAdapter<?>> adapters) {
            this.eventClass = eventClass;
            this.adapters = adapters;
        }
    }
}
