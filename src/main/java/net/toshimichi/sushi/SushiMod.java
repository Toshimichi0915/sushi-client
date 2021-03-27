package net.toshimichi.sushi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.toshimichi.sushi.command.Commands;
import net.toshimichi.sushi.command.client.HelpCommand;
import net.toshimichi.sushi.command.client.PrefixCommand;
import net.toshimichi.sushi.command.client.SetCommand;
import net.toshimichi.sushi.command.client.ToggleCommand;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.GsonConfigurations;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.WorldLoadEvent;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.simple.SimpleTheme;
import net.toshimichi.sushi.handlers.*;
import net.toshimichi.sushi.handlers.forge.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

@Mod(modid = "sushi", name = "Sushi Client", version = "1")
public class SushiMod {

    private static final Gson gson;

    static {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private final File baseDir = new File("./sushi");
    private final File modConfigFile = new File(baseDir, "config.json");
    private final File themeDir = new File(baseDir, "themes");
    private ModConfig modConfig;
    private final HashMap<File, GsonConfigurations> configs = new HashMap<>();

    private Theme loadTheme(File file, Function<Configurations, Theme> func) {
        JsonObject object = null;
        try {
            if (file.exists())
                object = gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (object == null)
            object = new JsonObject();
        GsonConfigurations conf = new GsonConfigurations(gson);
        conf.load(object);
        configs.put(file, conf);
        Theme theme = func.apply(conf);

        if (!file.exists()) {
            try {
                FileUtils.writeStringToFile(file, gson.toJson(conf.save()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return theme;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        // load config
        try {
            String contents = FileUtils.readFileToString(modConfigFile, StandardCharsets.UTF_8);
            modConfig = gson.fromJson(contents, ModConfig.class);
        } catch (IOException e) {
            modConfig = new ModConfig();
        }

        // add themes
        ArrayList<Theme> themes = new ArrayList<>();
        Theme fallbackTheme = loadTheme(new File(themeDir, "simple.json"), SimpleTheme::new);
        themes.add(fallbackTheme);

        for (Theme theme : Sushi.getThemes()) {
            if (theme.getId().equals(modConfig.theme)) {
                fallbackTheme = theme;
                break;
            }
        }
        Sushi.setThemes(themes);
        Sushi.setDefaultTheme(fallbackTheme);

        // load profile
        GsonProfiles profiles = new GsonProfiles(new File(baseDir, "profiles"), gson);
        Profile profile = profiles.load(modConfig.name);
        Sushi.setProfiles(profiles);
        Sushi.setProfile(profile);
        profile.load();

        // register events
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new MouseInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        MinecraftForge.EVENT_BUS.register(new ClientChatHandler());
        EventHandlers.register(new KeyFixHandler());
        EventHandlers.register(new KeybindHandler());
        EventHandlers.register(new ComponentMouseHandler());
        EventHandlers.register(new ComponentRenderHandler());
        EventHandlers.register(new ComponentKeyHandler());
        EventHandlers.register(new GameFocusHandler());
        EventHandlers.register(new ConfigurationHandler());
        EventHandlers.register(new CommandHandler());
        EventHandlers.register(new RotateHandler());
        EventHandlers.register(this);

        Commands.register(new HelpCommand());
        Commands.register(new ToggleCommand());
        Commands.register(new PrefixCommand());
        Commands.register(this, new SetCommand());
    }

    @net.toshimichi.sushi.events.EventHandler(timing = EventTiming.PRE)
    public void onLoadWorld(WorldLoadEvent e) {
        if (e.getClient() != null) return;
        try {
            FileUtils.writeStringToFile(modConfigFile, gson.toJson(modConfig), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class ModConfig {
        String name = "default";
        String theme = "simple";
    }
}
