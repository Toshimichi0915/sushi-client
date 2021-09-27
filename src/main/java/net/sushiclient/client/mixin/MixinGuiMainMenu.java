package net.sushiclient.client.mixin;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.Vec2f;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.utils.render.account.GuiAccounts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMainMenu.class, priority = 3000)
public class MixinGuiMainMenu extends GuiScreen {

    // random button id
    private static final int BUTTON_ID = 786714;

    private boolean isIn(GuiButton guiButton, Vec2f point) {
        return guiButton.x - 1 <= point.x &&
                guiButton.y - 1 <= point.y &&
                point.x <= guiButton.x + guiButton.width + 1 &&
                point.y <= guiButton.y + guiButton.height + 1;
    }

    private boolean intersects(int index, int y1, int y2) {
        Vec2f[] points = new Vec2f[]{
                new Vec2f(width / 2 - 100, y1 + y2 * index),
                new Vec2f(width / 2 + 100, y1 + y2 * index),
                new Vec2f(width / 2 - 100, y1 + y2 * index + 20),
                new Vec2f(width / 2 + 100, y1 + y2 * index + 20),
        };
        for (GuiButton button : buttonList) {
            for (Vec2f point : points) {
                if (isIn(button, point)) return true;
            }
        }
        return false;
    }

    @Inject(at = @At("RETURN"), method = "initGui")
    private void initGui(CallbackInfo ci) {
        int y1 = height / 4 + 48 + 12;
        int y2 = 24;

        int index = 0;
        while (intersects(index, y1, y2)) {
            index++;
        }
        buttonList.add(new GuiButton(BUTTON_ID, width / 2 - 100, y1 + y2 * index, "Accounts"));
    }

    @Inject(at = @At("HEAD"), method = "actionPerformed")
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == BUTTON_ID) {
            mc.displayGuiScreen(new GuiAccounts(this, Sushi.getMojangAccounts()));
        }
    }
}
