package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public class IntParser implements TypeParser<Integer> {

    @Override
    public Integer parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing integer at index " + index);
        try {
            return Integer.parseInt(args.pop());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid integer at index " + index);
        }
    }

    @Override
    public String getToken() {
        return "int";
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
