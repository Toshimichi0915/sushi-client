package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;

import java.util.Stack;

public interface TypeParser<T> {

    String UNMODIFIABLE_ERROR = "This setting cannot be changed";

    default T parse(int index, Stack<String> args) throws ParseException {
        throw new ParseException(UNMODIFIABLE_ERROR);
    }

    default T parse(int index, Stack<String> args, T original) throws ParseException {
        return parse(index, args);
    }

    String getToken();

    Class<T> getType();

    default int getPriority() {
        return 1000;
    }
}
