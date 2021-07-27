package net.toshimichi.sushi.mixin;

import net.minecraft.client.settings.KeyBinding;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.input.KeyDownCheckEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
abstract public class MixinKeyBinding {

    @Shadow(aliases = "pressed")
    private boolean pressed;

    @Inject(at = @At("HEAD"), method = "isKeyDown", cancellable = true)
    public void iskeyDown(CallbackInfoReturnable<Boolean> cir) {
        KeyBinding k = (KeyBinding) (Object) this;
        boolean original = pressed && k.getKeyConflictContext().isActive() && k.getKeyModifier().isActive(k.getKeyConflictContext());
        KeyDownCheckEvent event = new KeyDownCheckEvent(k, pressed, original);
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getResult());
        cir.cancel();
    }
}
