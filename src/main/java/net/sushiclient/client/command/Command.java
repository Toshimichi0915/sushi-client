package net.sushiclient.client.command;

import java.util.List;

/**
 * Represents single command.
 */
public interface Command extends SubCommand {

    /**
     * Executes the command.
     *
     * @param out  the handler messages are output to
     * @param args command arguments
     */
    default void execute(Logger out, List<String> args) {
        execute(out, args, args);
    }

    /**
     * Gets all sub commands of the command.
     *
     * @return all sub commands of the command
     */
    default SubCommand[] getSubCommands() {
        return new SubCommand[0];
    }
}
