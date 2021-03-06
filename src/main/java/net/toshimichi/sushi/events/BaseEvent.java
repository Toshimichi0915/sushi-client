package net.toshimichi.sushi.events;

public class BaseEvent implements Event {

    private final EventTiming timing;

    public BaseEvent(EventTiming timing) {
        this.timing = timing;
    }

    @Override
    public EventTiming getTiming() {
        return timing;
    }
}
