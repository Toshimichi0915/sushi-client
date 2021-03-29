package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;

import java.awt.Color;

public class HudConstants {
    public final Configuration<Color> textColor;
    public final Configuration<String> font;

    public HudConstants(Configurations c) {
        textColor = c.get("hud.text_color", "Text Color", null, Color.class, Color.WHITE);
        font = c.get("hud.font", "Font", null, String.class, "Calibri");
    }
}
