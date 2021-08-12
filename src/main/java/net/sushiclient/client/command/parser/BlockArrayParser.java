package net.sushiclient.client.command.parser;

import net.sushiclient.client.command.ParseException;
import net.sushiclient.client.config.data.BlockName;

import java.util.Stack;

public class BlockArrayParser implements TypeParser<BlockName[]> {

    @Override
    public BlockName[] parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing list at index " + index);
        String text = args.pop();
        String[] split = text.split(",");
        BlockName[] result = new BlockName[split.length];
        for (int i = 0; i < split.length; i++) {
            BlockName name = BlockName.fromName(split[i]);
            if (name == null)
                throw new ParseException(split[i] + " is not a valid block name");
            result[i] = name;
        }
        return result;
    }

    @Override
    public String getToken() {
        return "blocks";
    }

    @Override
    public Class<BlockName[]> getType() {
        return BlockName[].class;
    }
}
