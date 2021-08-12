package net.sushiclient.client.mixin;

import net.minecraft.client.settings.GameSettings;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.GameSettingsSaveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class MixinGameSettings {

    @Inject(at = @At("HEAD"), method = "saveOptions", cancellable = true)
    public void onPreSaveOptions(CallbackInfo ci) {
        GameSettingsSaveEvent event = new GameSettingsSaveEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "saveOptions")
    public void onPostSaveOptions(CallbackInfo ci) {
        EventHandlers.callEvent(new GameSettingsSaveEvent(EventTiming.POST));
    }
}
