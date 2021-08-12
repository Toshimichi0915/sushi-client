package net.sushiclient.client.events.world;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class ThunderStrengthGetEvent extends BaseEvent {

    private final float delta;
    private float value;

    public ThunderStrengthGetEvent(EventTiming timing, float delta, float value) {
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
