package net.toshimichi.sushi.command;

import net.toshimichi.sushi.command.annotation.AnnotationCommand;
import net.toshimichi.sushi.command.parser.*;
import net.toshimichi.sushi.hwid.annotations.AsyncAuthentication;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Commands {
    private static final HashSet<CommandMap> maps = new HashSet<>();
    private static final HashSet<TypeParser<?>> parsers = new HashSet<>();

    static {
        addTypeParser(new IntParser());
        addTypeParser(new DoubleParser());
        addTypeParser(new IntRangeParser());
        addTypeParser(new DoubleRangeParser());
        addTypeParser(new CharParser());
        addTypeParser(new StringParser());
        addTypeParser(new BooleanParser());
        addTypeParser(new MessageHandlerParser());
        addTypeParser(new ModuleParser());
        addTypeParser(new BlockArrayParser());
        addTypeParser(new StringArrayParser());
    }

    public static List<Command> getCommands() {
        return maps.stream().map(map -> map.command).collect(Collectors.toList());
    }

    public static Set<TypeParser<?>> getTypeParsers() {
        return new HashSet<>(parsers);
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeParser<T> findParser(Class<T> c) throws ParseException {
        return (TypeParser<T>) getTypeParsers().stream()
                .filter(p -> c.isAssignableFrom(p.getType()))
                .min(Comparator.comparingInt(TypeParser::getPriority))
                .orElseThrow(() -> new ParseException(TypeParser.UNMODIFIABLE_ERROR));
    }

    public static void register(Object obj) {
        AnnotationCommand command = AnnotationCommand.newCommand(obj);
        if (command == null)
            throw new IllegalArgumentException(obj.getClass().getCanonicalName() + " is not valid command handler");
        maps.add(new CommandMap(obj, command));
    }

    public static void register(Object obj, Command command) {
        maps.add(new CommandMap(obj, command));
    }

    public static void unregister(Object o) {
        maps.removeIf(map -> map.object.equals(o));
    }

    private static CommandMap getCommandMap(String name) {
        for (CommandMap map : maps) {
            for (String alias : map.command.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return map;
                }
            }
            if (map.command.getName().equalsIgnoreCase(name))
                return map;
        }
        return null;
    }

    private static String completeCommand(String name) {
        name = name.toLowerCase();
        for (CommandMap map : maps) {
            for (String alias : map.command.getAliases()) {
                if (alias.toLowerCase().startsWith(name)) {
                    return alias;
                }
            }
            if (map.command.getName().toLowerCase().startsWith(name))
                return map.command.getName();
        }
        return null;
    }

    public static String complete(String command) {
        if (command.isEmpty()) return "";
        boolean lastEmpty = command.charAt(command.length() - 1) == ' ';
        List<String> args = Arrays.asList(command.split("\\s+"));
        String name = args.get(0);
        args = args.size() > 1 ? args.subList(1, args.size()) : Collections.emptyList();
        CommandMap map;
        String commandName;
        if (lastEmpty) {
            map = getCommandMap(name);
            commandName = name;
        } else {
            String completedCommand = completeCommand(name);
            if (completedCommand == null) return "";
            map = getCommandMap(completedCommand);
            commandName = completedCommand;
        }
        if (map == null) return "";

        List<String> completed = map.command.complete(args);
        ArrayList<String> list = new ArrayList<>(completed.size() + 1);
        list.add(commandName);
        list.addAll(completed);
        Matcher matcher = Pattern.compile("(\\s+)").matcher(command);
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s);
            if (matcher.find()) builder.append(matcher.group());
            builder.append(' ');
        }
        return builder.toString();
    }

    @AsyncAuthentication
    public static boolean execute(MessageHandler out, String name, List<String> args) {
        CommandMap map = getCommandMap(name);
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
