package net.toshimichi.sushi.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.utils.GuiUtils;
import net.toshimichi.sushi.utils.TextPreview;

import java.text.DecimalFormat;

public class CoordinatesComponent extends BaseComponent implements HudElementComponent {
    private static final DecimalFormat FORMATTER = new DecimalFormat(".#");
    private final HudConstants constants;
    private final Configuration<String> format;

    public CoordinatesComponent(HudConstants constants, Configurations configurations) {
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
        TextPreview preview = GuiUtils.prepareText(text, constants.font.getValue(), constants.textColor.getValue(), 10, true);
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
