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

    @SubCommand(value = "clone", syntax = "<name>")
    public void onClone(Logger logger, String name) {
        String old = Sushi.getProfiles().getName(Sushi.getProfile());
        if (old == null) {
            logger.send(LogLevel.ERROR, "Current profile could not be cloned");
            return;
        }
        Profile profile = Sushi.getProfiles().clone(old, name);
        if (profile == null) {
            logger.send(LogLevel.INFO, "Could not clone the profile");
        } else {
            logger.send(LogLevel.INFO, "Cloned the profile");
        }
    }

    @SubCommand(value = "remove", syntax = "<name>")
    public void onRemove(Logger logger, String name) {
        boolean successful = Sushi.getProfiles().remove(name);
        if (successful) {
            logger.send(LogLevel.INFO, "Removed the profile");
        } else {
            logger.send(LogLevel.INFO, "Could not delete the profile");
        }
    }

    @SubCommand(value = "set", syntax = "<name>")
    public void onSet(Logger logger, String str) {
        Profile profile = Sushi.getProfiles().load(str);

        Sushi.getProfile().getModules().disable();
        Sushi.getProfile().save();

        Sushi.setProfile(profile);
        Sushi.getProfile().load();
        Sushi.getProfile().getModules().enable();
        logger.send(LogLevel.INFO, "Changed the profile to " + str);
    }
}
