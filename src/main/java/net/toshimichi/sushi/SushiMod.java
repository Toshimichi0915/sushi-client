package net.toshimichi.sushi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.GsonConfigurations;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.gui.theme.Theme;
import net.toshimichi.sushi.gui.theme.simple.SimpleTheme;
import net.toshimichi.sushi.handlers.*;
import net.toshimichi.sushi.handlers.forge.ClientTickHandler;
import net.toshimichi.sushi.handlers.forge.KeyInputHandler;
import net.toshimichi.sushi.handlers.forge.MouseInputHandler;
import net.toshimichi.sushi.handlers.forge.RenderTickHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Function;

@Mod(modid = "sushi", name = "Sushi Client", version = "1.0")
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
        Sushi.getThemes().add(theme);

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
        Theme fallbackTheme = loadTheme(new File(themeDir, "simple.json"), SimpleTheme::new);
        // TODO Add more themes

        for (Theme theme : Sushi.getThemes()) {
            if (theme.getId().equals(modConfig.themeName)) {
                fallbackTheme = theme;
                break;
            }
        }
        Sushi.setDefaultTheme(fallbackTheme);

        // load profile
        GsonProfiles profiles = new GsonProfiles(new File(baseDir, "profiles"), gson);
        Profile profile = profiles.load(modConfig.name);
        profile.load();
        Sushi.setProfiles(profiles);
        Sushi.setProfile(profile);

        // register events
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new MouseInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        EventHandlers.register(new KeybindHandler());
        EventHandlers.register(new ComponentMouseHandler());
        EventHandlers.register(new ComponentRenderHandler());
        EventHandlers.register(new ComponentKeyHandler());
        EventHandlers.register(new GameFocusHandler());
        EventHandlers.register(new ConfigurationHandler());
    }

    private static class ModConfig {
        String name = "default";
        String themeName = "simple";
    }
}
