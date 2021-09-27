package net.sushiclient.client.command.client;

import net.sushiclient.client.Profile;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.SubCommand;

@CommandAlias(value = "profile", description = "Edits profiles")
public class ProfileCommand {

    @SubCommand("list")
    public void onList(Logger logger) {
        logger.send(LogLevel.INFO, "Profiles: ");
        for (String str : Sushi.getProfiles().getAll()) {
            if (str.equals(Sushi.getProfiles().getName(Sushi.getProfile()))) {
                logger.send(LogLevel.INFO, "  " + str + " (Used)");
            } else {
                logger.send(LogLevel.INFO, "  " + str);
            }
        }
    }

    @SubCommand(value = "save", syntax = "<name>")
    public void onSave() {
        Sushi.getProfile().save();
    }

    @SubCommand(value = "set", syntax = "<name>")
    public void onSet(String str) {
        Profile profile = Sushi.getProfiles().load(str);

        Sushi.getProfile().getModules().disable();
        Sushi.getProfile().save();

        Sushi.setProfile(profile);
        Sushi.getProfile().load();
        Sushi.getProfile().getModules().enable();
    }
}
