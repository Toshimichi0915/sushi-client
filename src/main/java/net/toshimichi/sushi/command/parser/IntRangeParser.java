package net.toshimichi.sushi.command.parser;

import net.toshimichi.sushi.command.ParseException;
import net.toshimichi.sushi.config.data.IntRange;

import java.util.Stack;

public class IntRangeParser implements TypeParser<IntRange> {

    @Override
    public IntRange parse(int index, Stack<String> args, IntRange original) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing integer at index " + index);
        try {
            return new IntRange(Integer.parseInt(args.pop()),
                    original.getTop(), original.getBottom(), original.getStep());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid integer at index " + index);
        }
    }

    @Override
    public String getToken() {
        return "double_range";
    }

    @Override
    public Class<IntRange> getType() {
        return IntRange.class;
    }
}
