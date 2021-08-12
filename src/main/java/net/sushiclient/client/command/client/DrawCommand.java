package net.sushiclient.client.command.client;

import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.modules.Module;

@CommandAlias(value = "draw", description = "Shows/hides a module from HUD")
public class DrawCommand {

    @Default
    public void onDefault(Logger out, Module module) {
        module.setVisible(!module.isVisible());
        out.send(LogLevel.INFO, module.getName() + " is now " + (module.isVisible() ? "shown" : "hidden"));
    }
}
