package net.sushiclient.client.command.client;

import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.modules.Module;

@CommandAlias(value = "toggle", aliases = "t", description = "Toggles a module", syntax = "<module>")
public class ToggleCommand {

    @Default
    public void onDefault(Logger out, Module module) {
        module.setEnabled(!module.isEnabled());
        out.send(LogLevel.INFO, module.getName() + " has been " + (module.isEnabled() ? "enabled" : "disabled"));
    }
}
