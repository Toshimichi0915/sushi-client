package net.sushiclient.client.gui.hud.elements;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;
import net.sushiclient.client.utils.TpsUtils;

import java.text.DecimalFormat;

public class TpsComponent extends TextElementComponent {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public TpsComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        this.format = getConfiguration("tps.format", "TPS Format", null, String.class, "{tps} TPS");
    }

    @Override
    protected String getText() {
        return format.getValue().replace("{tps}", FORMATTER.format(TpsUtils.getTps()));
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