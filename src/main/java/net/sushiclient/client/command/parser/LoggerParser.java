package net.sushiclient.client.command.parser;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.ParseException;

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
