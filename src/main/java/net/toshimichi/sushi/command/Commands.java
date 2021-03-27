package net.toshimichi.sushi.command;

import net.toshimichi.sushi.command.annotation.AnnotationCommand;
import net.toshimichi.sushi.command.parser.IntParser;
import net.toshimichi.sushi.command.parser.MessageHandlerParser;
import net.toshimichi.sushi.command.parser.StringParser;
import net.toshimichi.sushi.command.parser.TypeParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Commands {
    private static final HashSet<CommandMap> maps = new HashSet<>();
    private static final HashSet<TypeParser<?>> parsers = new HashSet<>();

    static {
        addTypeParser(new IntParser());
        addTypeParser(new StringParser());
        addTypeParser(new MessageHandlerParser());
    }

    public static void register(Object obj) {
        AnnotationCommand command = AnnotationCommand.newCommand(obj, parsers);
        if (command == null)
            throw new IllegalArgumentException(obj.getClass().getCanonicalName() + " is not valid command handler");
        maps.add(new CommandMap(obj, command));
    }

    public static void unregister(Object o) {
        maps.removeIf(map -> map.object.equals(o));
    }

    private static CommandMap getCommandMap(String name, boolean equals) {
        name = name.toLowerCase();
        for (CommandMap map : maps) {
            for (String alias : map.command.getAliases()) {
                if (alias.equalsIgnoreCase(name) || (!equals && alias.toLowerCase().startsWith(name))) {
                    return map;
                }
            }
            if (map.command.getName().equalsIgnoreCase(name) || (!equals && map.command.getName().toLowerCase().startsWith(name)))
                return map;
        }
        return null;
    }

    public static List<String> complete(String name, List<String> args) {
        CommandMap map = getCommandMap(name, false);
        if (map == null) return Collections.emptyList();
        return map.command.complete(new ArrayList<>(args));
    }

    public static boolean execute(MessageHandler out, String name, List<String> args) {
        CommandMap map = getCommandMap(name, true);
        if (map != null) {
            map.command.execute(out, new ArrayList<>(args));
            return true;
        } else {
            return false;
        }
    }

    public static void addTypeParser(TypeParser<?> parser) {
        parsers.add(parser);
    }

    private static class CommandMap {
        final Object object;
        final Command command;

        public CommandMap(Object object, Command command) {
            this.object = object;
            this.command = command;
        }
    }
}
