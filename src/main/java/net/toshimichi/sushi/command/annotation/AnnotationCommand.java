package net.toshimichi.sushi.command.annotation;

import net.toshimichi.sushi.command.*;
import net.toshimichi.sushi.command.parser.TypeParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

public class AnnotationCommand extends BaseCommand {

    private final String name;
    private final String[] aliases;
    private final String description;
    private final String syntax;
    private final ArrayList<Command> subCommands;
    private final Object object;
    private final Method method;
    private final TypeParser<?>[] parsers;

    private AnnotationCommand(String name, String[] aliases, String description, String syntax, ArrayList<Command> subCommands, Object object, Method method, TypeParser<?>[] parsers) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.syntax = syntax;
        this.subCommands = subCommands;
        this.object = object;
        this.method = method;
        this.parsers = parsers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return description.isEmpty() ? null : description;
    }

    @Override
    protected String getSyntax() {
        return syntax.isEmpty() ? null : syntax;
    }

    @Override
    public Command[] getSubCommands() {
        return subCommands.toArray(new Command[0]);
    }

    @Override
    protected void executeDefault(MessageHandler out, List<String> args, List<String> original) {
        if (method == null || parsers == null) {
            super.executeDefault(out, args, original);
            return;
        }
        Stack<String> stack = new Stack<>();
        stack.addAll(args);
        Object[] objects = new Object[parsers.length];
        int index = original.size() - args.size();
        for (int i = 0; i < parsers.length; i++) {
            int stackSize = stack.size();
            try {
                objects[i] = parsers[i].parse(index, stack);
                index += stackSize - stack.size();
            } catch (ParseException e) {
                out.send(e.getMessage(), LogLevel.ERROR);
                return;
            }
        }
        try {
            method.invoke(object, objects);
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<TypeParser<?>> findTypeParsers(Method method, Collection<TypeParser<?>> parsers) {
        ArrayList<TypeParser<?>> result = new ArrayList<>(method.getParameterCount());
        for (Parameter parameter : method.getParameters()) {
            Token token = parameter.getAnnotation(Token.class);
            Stream<TypeParser<?>> stream = parsers.stream()
                    .filter(p -> parameter.getType().isAssignableFrom(p.getType()))
                    .sorted(Comparator.comparingInt(TypeParser::getPriority));
            if (token != null)
                stream = stream.filter(p -> p.getToken().equals(token.value()));
            Optional<TypeParser<?>> parser = stream.findFirst();
            if (!parser.isPresent())
                throw new IllegalArgumentException("No matching parser for method " + method.getDeclaringClass().getCanonicalName() + "#" + method.getName());
            result.add(parser.get());
        }
        return result;
    }

    public static AnnotationCommand newCommand(Object o, Collection<TypeParser<?>> parsers) {
        Class<?> c = o.getClass();
        CommandAlias alias = c.getAnnotation(CommandAlias.class);
        if (alias == null) return null;

        ArrayList<Command> subCommands = new ArrayList<>();
        Method defaultMethod = null;
        TypeParser<?>[] defaultParsers = null;
        for (Method method : c.getMethods()) {
            if (method.getAnnotation(Default.class) != null) {
                defaultMethod = method;
                defaultParsers = findTypeParsers(method, parsers).toArray(new TypeParser[0]);
            } else if (method.getAnnotation(SubCommand.class) != null) {
                SubCommand subCommand = method.getAnnotation(SubCommand.class);
                subCommands.add(new AnnotationCommand(subCommand.value(), subCommand.aliases(), subCommand.description(),
                        subCommand.syntax(), new ArrayList<>(), o, method, findTypeParsers(method, parsers).toArray(new TypeParser[0])));
            }
        }
        return new AnnotationCommand(alias.value(), alias.aliases(), alias.description(), alias.syntax(), subCommands, o, defaultMethod, defaultParsers);
    }
}
