package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.config.data.EspColor;

import java.awt.Color;

public class HudConstants {
    public final Configuration<EspColor> textColor;
    public final Configuration<String> font;

    public HudConstants(ConfigurationCategory c) {
        textColor = c.get("element. " + c.getId() + ".text_color", "Text Color", null, EspColor.class, new EspColor(Color.WHITE, true));
        font = c.get("element." + c.getId() + ".font", "Font", null, String.class, "Calibri");
    }
}
