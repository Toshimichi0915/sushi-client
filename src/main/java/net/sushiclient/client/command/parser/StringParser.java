package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public class StringParser implements TypeParser<String> {
    @Override
    public String parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing string at index " + index);
        return args.pop();
    }

    @Override
    public String getToken() {
        return "String";
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
