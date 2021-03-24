package net.toshimichi.sushi.gui.theme;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;

import java.awt.Color;

public class ThemeConstants {
    public final Configuration<Color> frameColor;
    public final Configuration<Color> menuBarColor;
    public final Configuration<Color> disabledColor;
    public final Configuration<Color> enabledColor;
    public final Configuration<Color> unselectedHoverColor;
    public final Configuration<Color> selectedHoverColor;
    public final Configuration<Color> textColor;
    public final Configuration<Color> backgroundColor;
    public final Configuration<Color> barColor;
    public final Configuration<Color> barBackgroundColor;
    public final Configuration<Color> textBoxBackgroundColor;
    public final Configuration<Color> textBoxTextColor;
    public final Configuration<Color> outlineColor;
    public final Configuration<String> font;

    public ThemeConstants(Configurations c) {
        frameColor = c.get("gui.frame_color", "Frame Color", null, Color.class, new Color(200, 90, 30));
        menuBarColor = c.get("gui.menu_bar_color", "Menu Bar Color", null, Color.class, new Color(30, 30, 30));
        disabledColor = c.get("gui.disabled_color", "Disabled Color", null, Color.class, new Color(40, 40, 40));
        enabledColor = c.get("gui.enabled_color", "Enabled Color", null, Color.class, new Color(140, 140, 255));
        unselectedHoverColor = c.get("gui.unselected_hover_color", "Unselected Hover Color", null, Color.class, new Color(30, 30, 30));
        selectedHoverColor = c.get("gui.selected_hover_color", "Selected Hover Color", null, Color.class, new Color(100, 100, 230));
        textColor = c.get("gui.text_color", "Text Color", null, Color.class, new Color(255, 255, 255));
        backgroundColor = c.get("gui.background_color", "Background Color", null, Color.class, new Color(30, 30, 30));
        barColor = c.get("gui.bar_color", "Bar Color", null, Color.class, new Color(140, 140, 255));
        barBackgroundColor = c.get("gui.bar_background_color", "Menu Bar Background Color", null, Color.class, new Color(30, 30, 30));
        textBoxBackgroundColor = c.get("gui.text_box_background_color", "Text Background Color", null, Color.class, new Color(30, 30, 30));
        textBoxTextColor = c.get("gui.text_box_text_color", "Text Color in Text Box", null, Color.class, new Color(30, 30, 30));
        outlineColor = c.get("gui.outline_color", "Outline Color", null, Color.class, new Color(60, 60, 60));
        font = c.get("gui.font", "Font", null, String.class, "Calibri");
    }
}
