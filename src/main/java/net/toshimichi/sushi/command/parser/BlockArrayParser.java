package net.toshimichi.sushi.command.parser;

import net.minecraft.block.Block;
import net.toshimichi.sushi.command.ParseException;

import java.util.Stack;

public class BlockArrayParser implements TypeParser<String[]> {

    @Override
    public String[] parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing list at index " + index);
        String text = args.pop();
        String[] split = text.split(",");
        for (String s : split) {
            if (Block.getBlockFromName(s) == null)
                throw new ParseException(s + " is not a valid block name");
        }
        return split;
    }

    @Override
    public String getToken() {
        return "blocks";
    }

    @Override
    public Class<String[]> getType() {
        return String[].class;
    }
}
