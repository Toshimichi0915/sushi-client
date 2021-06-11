package net.toshimichi.sushi.events.world;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class RainStrengthGetEvent extends BaseEvent {

    private final float delta;
    private float value;

    public RainStrengthGetEvent(EventTiming timing, float delta, float value) {
        super(timing);
        this.delta = delta;
        this.value = value;
    }

    public float getDelta() {
        return delta;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
