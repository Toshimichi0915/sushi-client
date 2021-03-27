package net.toshimichi.sushi.command.parser;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.command.ParseException;

import java.util.Stack;

public class MessageHandlerParser implements TypeParser<MessageHandler> {
    @Override
    public MessageHandler parse(int index, Stack<String> args) throws ParseException {
        return Sushi.getProfile().getMessageHandler();
    }

    @Override
    public String getToken() {
        return "message";
    }

    @Override
    public Class<MessageHandler> getType() {
        return MessageHandler.class;
    }
}
