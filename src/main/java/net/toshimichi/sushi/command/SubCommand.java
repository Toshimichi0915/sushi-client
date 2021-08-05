package net.toshimichi.sushi.command;

import java.util.List;

public interface SubCommand {

    /**
     * Gets the command name.
     *
     * @return the command name of this command
     */
    String getName();

    /**
     * Gets the aliases for this command.
     * Aliases can be used instead of command name.
     *
     * @return the aliases of this command
     */
    default String[] getAliases() {
        return new String[0];
    }

    /**
     * Gets the description of this command.
     *
     * <p>This description cannot be used to pragmatically process anything.
     * This method is purely for convenience for users.</p>
     *
     * @return the description of this method
     */
    String getDescription();

    /**
     * Gets the command syntax.
     * the arguments must be split by space.
     *
     * @return the command syntax
     */
    List<String> complete(List<String> args);

    void execute(Logger out, List<String> args, List<String> original);
}
