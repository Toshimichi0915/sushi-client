package net.toshimichi.sushi.command.client;

import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;
import net.toshimichi.sushi.modules.Module;

@CommandAlias(value = "toggle", aliases = "t", description = "Toggles a module")
public class ToggleCommand {

    @Default
    public void onDefault(MessageHandler out, Module module) {
        module.setEnabled(!module.isEnabled());
        out.send(module.getName() + " has been " + (module.isEnabled() ? "enabled" : "disabled"), LogLevel.INFO);
    }
}
