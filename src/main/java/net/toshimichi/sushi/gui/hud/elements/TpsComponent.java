package net.toshimichi.sushi.gui.hud.elements;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.hud.BaseHudElementComponent;
import net.toshimichi.sushi.gui.hud.HudElementComponent;
import net.toshimichi.sushi.utils.TpsUtils;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

import java.text.DecimalFormat;

public class TpsComponent extends BaseHudElementComponent implements HudElementComponent {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public TpsComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        this.format = configurations.get("element.tps.format", "TPS Format", null, String.class, "{tps} TPS");
    }

    @Override
    public void onRender() {
        String text = format.getValue().replace("{tps}", FORMATTER.format(TpsUtils.getTps()));
        TextPreview preview = GuiUtils.prepareText(text, getTextSettings("text").getValue());
        preview.draw(getWindowX() + 1, getWindowY() + 1);
        setWidth(preview.getWidth() + 3);
        setHeight(preview.getHeight() + 4);
    }

    @Override
    public String getId() {
        return "tps";
    }

    @Override
    public String getName() {
        return "TPS";
    }
}