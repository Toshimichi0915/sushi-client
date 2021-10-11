package net.sushiclient.client.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.DimensionType;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;

import java.text.DecimalFormat;

public class OverworldComponent extends TextElementComponent {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public OverworldComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        this.format = getConfiguration("format", "Format", null, String.class,
                "Overworld: {x} {z}");
    }

    @Override
    protected String getText() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        WorldClient world = Minecraft.getMinecraft().world;
        if (player == null || world == null) return "";
        double x, z;
        if (world.provider.getDimensionType() == DimensionType.NETHER) {
            x = player.posX * 8;
            z = player.posZ * 8;
        } else {
            x = player.posX;
            z = player.posZ;
        }
        return format.getValue().replace("{x}", FORMATTER.format(x))
                .replace("{z}", FORMATTER.format(z));
    }

    @Override
    public String getId() {
        return "overworld";
    }

    @Override
    public String getName() {
        return "Overworld";
    }
}
