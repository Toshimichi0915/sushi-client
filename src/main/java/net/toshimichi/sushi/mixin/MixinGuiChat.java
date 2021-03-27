package net.toshimichi.sushi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.command.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    private static final int SHADOW_COLOR = new Color(200, 200, 200).getRGB();

    @Shadow
    protected GuiTextField inputField;

    @Inject(at = @At("HEAD"), method = "drawScreen")
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        String text = inputField.getText();
        if (text.isEmpty()) return;
        if (text.charAt(0) != Sushi.getProfile().getPrefix()) return;
        String complete = Commands.complete(text.substring(1));
        Minecraft.getMinecraft().fontRenderer.drawString(Sushi.getProfile().getPrefix() + complete, inputField.x, inputField.y, SHADOW_COLOR);
    }
}