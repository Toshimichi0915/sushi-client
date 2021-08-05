package net.toshimichi.sushi.command.client;

import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.Logger;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;
import net.toshimichi.sushi.modules.Module;

@CommandAlias(value = "draw", description = "Shows/hides a module from HUD")
public class DrawCommand {

    @Default
    public void onDefault(Logger out, Module module) {
        module.setVisible(!module.isVisible());
        out.send(LogLevel.INFO, module.getName() + " is now " + (module.isVisible() ? "shown" : "hidden"));
    }
}
