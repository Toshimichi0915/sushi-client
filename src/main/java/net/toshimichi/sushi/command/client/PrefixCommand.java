package net.toshimichi.sushi.command.client;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.Logger;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;

@CommandAlias("prefix")
public class PrefixCommand {

    @Default
    public void onDefault(Logger out, Character prefix) {
        Sushi.getProfile().setPrefix(prefix);
        out.send(LogLevel.INFO, "Set prefix to " + prefix);
    }
}
