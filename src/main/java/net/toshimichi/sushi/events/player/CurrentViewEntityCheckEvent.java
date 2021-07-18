package net.toshimichi.sushi.events.player;


import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

public class CurrentViewEntityCheckEvent extends BaseEvent {

    private boolean currentViewEntity;

    public CurrentViewEntityCheckEvent(EventTiming timing, boolean currentViewEntity) {
        super(timing);
        this.currentViewEntity = currentViewEntity;
    }

    public boolean isCurrentViewEntity() {
        return currentViewEntity;
    }

    public void setCurrentViewEntity(boolean currentViewEntity) {
        this.currentViewEntity = currentViewEntity;
    }
}
