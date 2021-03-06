package net.toshimichi.sushi;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.toshimichi.sushi.events.forge.KeyInputHandler;
import org.apache.logging.log4j.Logger;

@Mod(modid = "sushi", name = "Sushi Client", version = "1.0")
public class SushiMod {

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
    }
}
