package net.sushiclient.client.command.client;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.modules.Module;

import java.util.List;

@CommandAlias("restore")
public class RestoreCommand {

    @Default
    public void onDefault(Logger logger) {
        List<Module> modules = Sushi.getProfile().getModules().restoreAll();
        logger.send(LogLevel.INFO, "Restored " + modules.size() + " module(s)");
    }
}
