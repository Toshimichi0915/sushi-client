package net.sushiclient.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

@Mod(modid = "sushi", name = "Sushi Client", version = "1")
public class SushiMod {

    private static final Initializer INITIALIZER = new SushiInitializer();

    @EventHandler
    public void construct(FMLConstructionEvent event) {
        INITIALIZER.construct(event);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        INITIALIZER.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        INITIALIZER.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        INITIALIZER.postInit(event);
    }

    @EventHandler
    public void complete(FMLLoadCompleteEvent event) {
        INITIALIZER.complete(event);
    }
}
