package net.toshimichi.sushi.command.parser;

import net.toshimichi.sushi.command.ParseException;

import java.util.Stack;

public interface TypeParser<T> {
    T parse(int index, Stack<String> args) throws ParseException;

    String getToken();

    Class<T> getType();

    default int getPriority() {
        return 1000;
    }
}
