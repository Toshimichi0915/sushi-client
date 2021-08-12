package net.sushiclient.client.events.player;

import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class UserCheckEvent extends BaseEvent {

    private boolean user;

    public UserCheckEvent(EventTiming timing, boolean user) {
        super(timing);
        this.user = user;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }
}
