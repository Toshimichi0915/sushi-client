package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public class DoubleParser implements TypeParser<Double> {
    @Override
    public Double parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing double at index " + index);
        try {
            return Double.parseDouble(args.pop());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid double at index " + index);
        }
    }

    @Override
    public String getToken() {
        return "double";
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
