package net.toshimichi.sushi.command.parser;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.Logger;
import net.toshimichi.sushi.command.ParseException;

import java.util.Stack;

public class LoggerParser implements TypeParser<Logger> {
    @Override
    public Logger parse(int index, Stack<String> args) throws ParseException {
        return Sushi.getProfile().getLogger();
    }

    @Override
    public String getToken() {
        return "message";
    }

    @Override
    public Class<Logger> getType() {
        return Logger.class;
    }
}
