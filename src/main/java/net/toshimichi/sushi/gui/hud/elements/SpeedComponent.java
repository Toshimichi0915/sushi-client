package net.toshimichi.sushi.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.hud.TextElementComponent;
import net.toshimichi.sushi.utils.player.SpeedUtils;

import java.text.DecimalFormat;

public class SpeedComponent extends TextElementComponent {

    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public SpeedComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        format = configurations.get("element." + id + ".format", "Format", null, String.class, "{m/s} m/s");
    }

    @Override
    protected String getText() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return "";
        return format.getValue().replace("{m/s}", FORMATTER.format(SpeedUtils.getMps(player)))
                .replace("{km/h}", FORMATTER.format(SpeedUtils.getKmph(player)));
    }
}
