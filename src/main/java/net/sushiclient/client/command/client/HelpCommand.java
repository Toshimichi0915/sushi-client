package net.sushiclient.client.command.client;

import net.sushiclient.client.command.Command;
import net.sushiclient.client.command.Commands;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;

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
