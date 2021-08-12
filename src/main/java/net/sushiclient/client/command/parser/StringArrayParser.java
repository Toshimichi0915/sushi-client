package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public class StringArrayParser implements TypeParser<String[]> {

    @Override
    public String[] parse(int index, Stack<String> args) throws ParseException {
        String[] result = args.toArray(new String[0]);
        while (!args.isEmpty()) args.pop();
        return result;
    }

    @Override
    public String getToken() {
        return "string-array";
    }

    @Override
    public Class<String[]> getType() {
        return String[].class;
    }
}
