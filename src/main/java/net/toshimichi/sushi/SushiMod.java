package net.toshimichi.sushi;

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
import net.toshimichi.sushi.modules.GsonModules;
import net.toshimichi.sushi.modules.Modules;

import java.io.File;

@Mod(modid = "sushi", name = "Sushi Client", version = "1.0")
public class SushiMod {

    private static Modules modules;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new MouseInputHandler());
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        EventHandlers.register(new KeybindHandler());
        EventHandlers.register(new ComponentMouseHandler());
        EventHandlers.register(new ComponentRenderHandler());
        EventHandlers.register(new ComponentKeyHandler());
        modules = new GsonModules(new File("./sushi.json"));
        modules.load();
    }

    public static Modules getModules() {
        return modules;
    }
}
