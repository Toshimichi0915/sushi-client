package net.toshimichi.sushi.gui.hud.elements;

import net.minecraft.client.Minecraft;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.hud.TextElementComponent;

public class FpsComponent extends TextElementComponent {

    private final Configuration<String> format;

    public FpsComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        this.format = getConfiguration("format", "Format", null, String.class,
                "FPS: {fps}");
    }

    @Override
    protected String getText() {
        return format.getValue().replace("{fps}", Integer.toString(Minecraft.getDebugFPS()));
    }
}
