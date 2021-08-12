package net.sushiclient.client.events;

import net.minecraft.client.Minecraft;

public class BaseEvent implements Event {

    private final EventTiming timing;
    private final boolean async;

    public BaseEvent(EventTiming timing, boolean async) {
        this.timing = timing;
        this.async = async;
    }

    public BaseEvent(EventTiming timing) {
        this(timing, !Minecraft.getMinecraft().isCallingFromMinecraftThread());
    }

    @Override
    public EventTiming getTiming() {
        return timing;
    }

    @Override
    public boolean isAsync() {
        return async;
    }
}
