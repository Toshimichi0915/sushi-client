package net.toshimichi.sushi.command.parser;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.ParseException;
import net.toshimichi.sushi.modules.Module;

import java.util.Stack;

public class ModuleParser implements TypeParser<Module> {
    @Override
    public Module parse(int index, Stack<String> args) throws ParseException {
        if (args.isEmpty())
            throw new ParseException("Missing module name/id at index " + index);
        String name = args.pop();
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!module.getName().equalsIgnoreCase(name) && !module.getId().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    @Override
    public String getToken() {
        return "module";
    }

    @Override
    public Class<Module> getType() {
        return Module.class;
    }
}
