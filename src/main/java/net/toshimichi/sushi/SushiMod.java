package net.toshimichi.sushi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.handlers.ComponentKeyHandler;
import net.toshimichi.sushi.handlers.ComponentMouseHandler;
import net.toshimichi.sushi.handlers.KeybindHandler;
import net.toshimichi.sushi.handlers.ComponentRenderHandler;
import net.toshimichi.sushi.handlers.forge.ClientTickHandler;
import net.toshimichi.sushi.handlers.forge.KeyInputHandler;
import net.toshimichi.sushi.handlers.forge.MouseInputHandler;
import net.toshimichi.sushi.handlers.forge.RenderTickHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Mod(modid = "sushi", name = "Sushi Client", version = "1.0")
public class SushiMod {

    private static final Gson gson;

    static {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private final File baseDir = new File("./sushi");
    private final File modConfigFile = new File(baseDir, "config.json");

    @EventHandler
    public void init(FMLInitializationEvent event) {

        // load config
        ModConfig config;
        try {
            String contents = FileUtils.readFileToString(modConfigFile, StandardCharsets.UTF_8);
            config = gson.fromJson(contents, ModConfig.class);
        } catch (IOException e) {
            config = new ModConfig();
        }
        GsonProfiles profiles = new GsonProfiles(new File(baseDir, "profiles"), gson);
        Sushi.setProfiles(profiles);
        Sushi.setProfile(profiles.load(config.name));

        // register events
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new MouseInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        EventHandlers.register(new KeybindHandler());
        EventHandlers.register(new ComponentMouseHandler());
        EventHandlers.register(new ComponentRenderHandler());
        EventHandlers.register(new ComponentKeyHandler());
    }

    private static class ModConfig {
        String name = "default";
    }
}
