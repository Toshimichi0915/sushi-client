package net.toshimichi.sushi.command;

import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;

@CommandAlias(value = "help", description = "Show all commands")
public class HelpCommand {

    @Default
    public void onDefault(MessageHandler out) {
        out.send("Commands: ", LogLevel.INFO);
        for (Command command : Commands.getCommands()) {
            if (command.getDescription() == null)
                out.send("  " + command.getName(), LogLevel.INFO);
            else
                out.send("  " + command.getName() + " - " + command.getDescription(), LogLevel.INFO);
        }
    }
}
