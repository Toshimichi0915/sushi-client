package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;
import net.sushiclient.client.config.data.DoubleRange;

import java.util.Stack;

public class DoubleRangeParser implements TypeParser<DoubleRange> {

    @Override
    public DoubleRange parse(int index, Stack<String> args, DoubleRange original) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing double at index " + index);
        try {
            return new DoubleRange(Double.parseDouble(args.pop()),
                    original.getTop(), original.getBottom(), original.getStep(), original.getDigits());
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid double at index " + index);
        }
    }

    @Override
    public String getToken() {
        return "double_range";
    }

    @Override
    public Class<DoubleRange> getType() {
        return DoubleRange.class;
    }
}
