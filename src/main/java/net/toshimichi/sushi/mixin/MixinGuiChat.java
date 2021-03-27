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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        List<String> args = Arrays.asList(text.split("\\s+"));
        String name = args.get(0).substring(1);
        args = args.subList(1, args.size());

        // draw shadow
        List<String> completed = Commands.complete(name, args);
        int x = inputField.x;
        int y = inputField.y;

        Matcher matcher = Pattern.compile("(\\s+)").matcher(text);
        StringBuilder builder = new StringBuilder(String.valueOf(Sushi.getProfile().getPrefix()));
        for (int i = 0; i < completed.size(); i++) {
            builder.append(completed.get(i));
            if (matcher.find()) builder.append(matcher.group());
            else if (i != 0) builder.append(' ');
        }
        Minecraft.getMinecraft().fontRenderer.drawString(builder.toString(), x, y, SHADOW_COLOR);
    }
}