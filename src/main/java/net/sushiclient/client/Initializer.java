package net.sushiclient.client;

import net.minecraftforge.fml.common.event.*;

public interface Initializer {

    default void construct(FMLConstructionEvent event) {}

    default void preInit(FMLPreInitializationEvent event) {}

    default void init(FMLInitializationEvent event) {}

    default void postInit(FMLPostInitializationEvent event) {}

    default void complete(FMLLoadCompleteEvent event) {}
}
