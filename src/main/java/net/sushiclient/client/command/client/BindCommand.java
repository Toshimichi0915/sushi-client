package net.sushiclient.client.command.client;

import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;
import net.sushiclient.client.modules.ActivationType;
import net.sushiclient.client.modules.Keybind;
import net.sushiclient.client.modules.Module;
import org.lwjgl.input.Keyboard;

@CommandAlias(value = "bind", description = "Sets keybinding for a module")
public class BindCommand {

    @Default
    public void onDefault(Logger out, Module module, String... keys) {
        int[] keyCode = new int[keys.length];
        for (int i = 0; i < keys.length; i++) {
            keyCode[i] = Keyboard.getKeyIndex(keys[i].toUpperCase());
            if (keyCode[i] == Keyboard.KEY_NONE) {
                out.send(LogLevel.ERROR, "Key name " + keys[i] + " is not recognized");
                return;
            }
        }
        module.setKeybind(new Keybind(ActivationType.TOGGLE, keyCode));
        out.send(LogLevel.INFO, "Changed keybinding for " + module.getName());
    }
}
