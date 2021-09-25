package net.sushiclient.client.command.client;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;

@CommandAlias(value = "prefix", description = "Change prefix of the client")
public class PrefixCommand {

    @Default
    public void onDefault(Logger out, Character prefix) {
        Sushi.getProfile().setPrefix(prefix);
        out.send(LogLevel.INFO, "Set prefix to " + prefix);
    }
}
