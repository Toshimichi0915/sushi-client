package net.sushiclient.client.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;

import java.text.DecimalFormat;

public class CoordinatesComponent extends TextElementComponent {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public CoordinatesComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        this.format = getConfiguration("format", "Format", null, String.class, "{x} {y} {z}");
    }

    @Override
    protected String getText() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return "";
        return format.getValue().replace("{x}", FORMATTER.format(player.posX))
                .replace("{y}", FORMATTER.format(player.posY))
                .replace("{z}", FORMATTER.format(player.posZ));
    }

    @Override
    public String getId() {
        return "coordinates";
    }

    @Override
    public String getName() {
        return "Coordinates";
    }
}
