package net.toshimichi.sushi.command.client;

import net.toshimichi.sushi.command.LogLevel;
import net.toshimichi.sushi.command.MessageHandler;
import net.toshimichi.sushi.command.annotation.CommandAlias;
import net.toshimichi.sushi.command.annotation.Default;
import net.toshimichi.sushi.modules.ActivationType;
import net.toshimichi.sushi.modules.Keybind;
import net.toshimichi.sushi.modules.Module;
import org.lwjgl.input.Keyboard;

@CommandAlias(value = "bind", description = "Sets keybinding for a module")
public class BindCommand {

    @Default
    public void onDefault(MessageHandler out, Module module, String... keys) {
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
