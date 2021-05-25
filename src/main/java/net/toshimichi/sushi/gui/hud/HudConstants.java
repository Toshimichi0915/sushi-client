package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;

import java.awt.Color;

public class HudConstants {
    public final Configuration<Color> textColor;
    public final Configuration<String> font;

    public HudConstants(ConfigurationCategory c) {
        textColor = c.get("element. " + c.getId() + ".text_color", "Text Color", null, Color.class, Color.WHITE);
        font = c.get("element." + c.getId() + ".font", "Font", null, String.class, "Calibri");
    }
}
