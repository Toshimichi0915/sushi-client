package net.sushiclient.client.gui.hud;

import net.sushiclient.client.gui.Component;

public interface HudElementComponent extends Component {

    String getId();

    String getName();

    boolean isActive();

    void setActive(boolean active);

}
