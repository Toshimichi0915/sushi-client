package net.toshimichi.sushi.command;

import net.toshimichi.sushi.command.parser.TypeParser;

import java.util.Stack;

/**
 * This is an exception thrown by {@link TypeParser} when {@link TypeParser#parse(Stack)} failed.
 */
public class ParseException extends Exception {

    public ParseException(String message) {
        super(message);
    }
}
