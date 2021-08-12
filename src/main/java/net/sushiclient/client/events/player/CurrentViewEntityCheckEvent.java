package net.sushiclient.client.events.player;


import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

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
