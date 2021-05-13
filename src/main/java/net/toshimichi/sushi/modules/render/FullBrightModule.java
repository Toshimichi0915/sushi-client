package net.toshimichi.sushi.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.client.GameSettingsSaveEvnet;
import net.toshimichi.sushi.modules.*;

public class FullBrightModule extends BaseModule {

    private final GameSettings settings = Minecraft.getMinecraft().gameSettings;
    private float oldGamma;

    public FullBrightModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        oldGamma = settings.gammaSetting;
        settings.gammaSetting = 15;
    }

    @Override
    public void onDisable() {
        settings.gammaSetting = oldGamma;
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPreSave(GameSettingsSaveEvnet e) {
        settings.gammaSetting = oldGamma;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostSave(GameSettingsSaveEvnet e) {
        settings.gammaSetting = 15;
    }

    @Override
    public String getDefaultName() {
        return "FullBright";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }
}
