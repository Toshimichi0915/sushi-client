package net.sushiclient.client.modules.client;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import org.apache.commons.lang3.RandomStringUtils;

public class IgnoreAbuseModule extends BaseModule {

    private final Configuration<DoubleRange> delay;
    private final Configuration<String> command;
    private int sleep;

    public IgnoreAbuseModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        this.command = provider.get("command", "Command", null, String.class, "/ignore ");
        this.delay = provider.get("delay", "Delay", null, DoubleRange.class, new DoubleRange(3, 10, 0, 0.2, 2));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        sleep = 0;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (sleep-- > 0) return;
        String cmd = command.getValue();
        int len = 256 - cmd.length();
        if (len < 1) return;
        String name = RandomStringUtils.randomAlphanumeric(len);
        getPlayer().sendChatMessage(cmd + name);
        sleep = (int) (delay.getValue().getCurrent() * 20);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "IgnoreAbuse";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.CLIENT;
    }
}
