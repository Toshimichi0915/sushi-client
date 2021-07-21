package net.toshimichi.sushi.command.client;

import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;
import net.toshimichi.sushi.modules.Module;

@CommandAlias(value = "draw", description = "Shows/hides a module from HUD")
public class DrawCommand {

    @Default
    public void onDefault(MessageHandler out, Module module) {
        module.setVisible(!module.isVisible());
        out.send(module.getName() + " is now " + (module.isVisible() ? "shown" : "hidden"), LogLevel.INFO);
    }
}
