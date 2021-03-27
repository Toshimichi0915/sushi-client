package net.toshimichi.sushi.command;

import java.util.List;

/**
 * Represents single command.
 */
public interface Command extends SubCommand {

    String getName();


    String[] getAliases();

    String getDescription();

    List<String> complete(List<String> args);

    /**
     * Executes the command.
     *
     * @param out  the handler messages are output to
     * @param args command arguments
     */
    default void execute(MessageHandler out, List<String> args) {
        execute(out, args, args);
    }

    /**
     * Gets all sub commands of the command.
     *
     * @return all sub commands of the command
     */
    SubCommand[] getSubCommands();
}
