package net.toshimichi.sushi.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.utils.GuiUtils;
import net.toshimichi.sushi.utils.TextPreview;

import java.text.DecimalFormat;

public class CoordinatesComponent extends BaseHudElementComponent implements HudElementComponent {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final HudConstants constants;
    private final Configuration<String> format;

    public CoordinatesComponent(Configurations configurations, HudConstants constants, String id, String name) {
        super(configurations, id, name);
        this.constants = constants;
        this.format = configurations.get("element.coordinates.format", "Coordinates Format", "Coordinates format", String.class, "{x} {y} {z}");
    }

    @Override
    public void onRender() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        String text = format.getValue().replace("{x}", FORMATTER.format(player.posX))
                .replace("{y}", FORMATTER.format(player.posY))
                .replace("{z}", FORMATTER.format(player.posZ));
        TextPreview preview = GuiUtils.prepareText(text, constants.font.getValue(), constants.textColor.getValue().getCurrentColor(), 10, true);
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
