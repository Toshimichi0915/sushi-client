package net.toshimichi.sushi.command.client;

import net.toshimichi.sushi.command.Command;
import net.toshimichi.sushi.command.Commands;
import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.Logger;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;

@CommandAlias(value = "help", description = "Shows all commands")
public class HelpCommand {

    @Default
    public void onDefault(Logger out) {
        out.send(LogLevel.INFO, "Commands: ");
        for (Command command : Commands.getCommands()) {
            if (command.getDescription() == null)
                out.send(LogLevel.INFO, "  " + command.getName());
            else
                out.send(LogLevel.INFO, "  " + command.getName() + " - " + command.getDescription());
        }
    }
}
