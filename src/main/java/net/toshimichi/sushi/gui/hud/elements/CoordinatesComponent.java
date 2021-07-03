package net.toshimichi.sushi.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.hud.BaseHudElementComponent;
import net.toshimichi.sushi.gui.hud.HudElementComponent;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

import java.text.DecimalFormat;

public class CoordinatesComponent extends BaseHudElementComponent implements HudElementComponent {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public CoordinatesComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        this.format = configurations.get("element.coordinates.format", "Coordinates Format", "Coordinates format", String.class, "{x} {y} {z}");
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        String text = format.getValue().replace("{x}", FORMATTER.format(player.posX))
                .replace("{y}", FORMATTER.format(player.posY))
                .replace("{z}", FORMATTER.format(player.posZ));
        TextPreview preview = GuiUtils.prepareText(text, getTextSettings("text").getValue());
        preview.draw(getWindowX() + 1, getWindowY() + 1);
        setWidth(preview.getWidth() + 3);
        setHeight(preview.getHeight() + 4);
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
