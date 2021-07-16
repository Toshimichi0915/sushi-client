package net.toshimichi.sushi.events.player;

import net.toshimichi.sushi.events.BaseEvent;
import net.toshimichi.sushi.events.EventTiming;

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
