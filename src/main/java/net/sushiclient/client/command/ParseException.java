package net.sushiclient.client.command;

import net.sushiclient.client.command.parser.TypeParser;

import java.util.Stack;

/**
 * This is an exception thrown by {@link TypeParser} when {@link TypeParser#parse(int, Stack)} or {@link TypeParser#parse(int, Stack, Object)}failed.
 */
public class ParseException extends Exception {

    public ParseException(String message) {
        super(message);
    }
}
