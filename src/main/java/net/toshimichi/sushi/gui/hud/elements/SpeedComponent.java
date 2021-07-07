package net.toshimichi.sushi.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.hud.BaseHudElementComponent;
import net.toshimichi.sushi.utils.player.SpeedUtils;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

import java.text.DecimalFormat;

public class SpeedComponent extends BaseHudElementComponent {

    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public SpeedComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        format = configurations.get("format", "Format", null, String.class, "{m/s} m/s");
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        String text = format.getValue().replace("{m/s}", FORMATTER.format(SpeedUtils.getMps(player)))
                .replace("{km/h}", FORMATTER.format(SpeedUtils.getKmph(player)));
        TextPreview preview = GuiUtils.prepareText(text, getTextSettings("text").getValue());
        preview.draw(getWindowX() + 1, getWindowY() + 1);
        setWidth(preview.getWidth() + 3);
        setHeight(preview.getHeight() + 4);
    }
}
