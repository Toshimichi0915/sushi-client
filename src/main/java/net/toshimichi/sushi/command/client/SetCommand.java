package net.toshimichi.sushi.command.client;

import net.toshimichi.sushi.command.*;
import net.toshimichi.sushi.command.parser.TypeParser;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.modules.Module;

import java.util.*;

public class SetCommand extends BaseCommand {

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Sets module settings to a specified value";
    }

    @Override
    protected String getSyntax() {
        return "<module> <key> <value>";
    }

    @SuppressWarnings("unchecked")
    private <T> TypeParser<T> findParser(Class<T> c) throws ParseException {
        return (TypeParser<T>) Commands.getTypeParsers().stream()
                .filter(p -> c.isAssignableFrom(p.getType()))
                .min(Comparator.comparingInt(TypeParser::getPriority))
                .orElseThrow(() -> new ParseException(TypeParser.UNMODIFIABLE_ERROR));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void executeDefault(MessageHandler out, List<String> args, List<String> original) {
        try {
            Stack<String> stack = new Stack<>();
            ArrayList<String> reverse = new ArrayList<>(args);
            Collections.reverse(reverse);
            stack.addAll(reverse);
            Module module = findParser(Module.class).parse(original.size() - stack.size(), stack);
            if (stack.isEmpty())
                throw new ParseException("A configuration name/id was missing at index " + original.size());
            String key = stack.pop();
            for (Configuration<?> conf : module.getConfigurations().getAll(true)) {
                if (!conf.getId().equalsIgnoreCase(key) && !conf.getName().equalsIgnoreCase(key)) continue;
                Object value = ((TypeParser<Object>) findParser(conf.getValueClass())).parse(original.size(), stack, conf.getValue());
                ((Configuration<Object>) conf).setValue(value);
                out.send("Changed configuration " + conf.getName(), LogLevel.INFO);
                return;
            }
            throw new ParseException("A configuration named " + key + " was not found");
        } catch (ParseException e) {
            out.send(e.getMessage(), LogLevel.ERROR);
        }
    }
}
