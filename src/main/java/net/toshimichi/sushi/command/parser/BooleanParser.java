package net.toshimichi.sushi.command.parser;

import net.toshimichi.sushi.command.ParseException;

import java.util.Stack;

public class BooleanParser implements TypeParser<Boolean> {
    @Override
    public Boolean parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing boolean at index " + index);
        try {
            return Boolean.parseBoolean(args.pop());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid boolean at index " + index);
        }
    }

    @Override
    public Boolean parse(int index, Stack<String> args, Boolean original) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing boolean at index " + index);
        String bool = args.pop();
        return bool.equalsIgnoreCase("toggle") ? !original : Boolean.parseBoolean(bool);
    }

    @Override
    public String getToken() {
        return "double";
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
