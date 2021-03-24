package net.toshimichi.sushi.gui.theme.simple.config;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.PanelComponent;
import net.toshimichi.sushi.gui.base.BaseComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.ThemeConstants;
import net.toshimichi.sushi.gui.theme.simple.SimpleTextComponent;
import net.toshimichi.sushi.utils.GuiUtils;
import net.toshimichi.sushi.utils.TextPreview;

public class SimpleStringComponent extends PanelComponent<Component> implements ConfigComponent<String> {

    private final Configuration<String> config;

    public SimpleStringComponent(ThemeConstants constants, Configuration<String> config) {
        this.config = config;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        add(new BaseComponent() {
            @Override
            public void onRender() {
                setHeight(10);
                GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.outlineColor.getValue());
                TextPreview preview = GuiUtils.prepareText(config.getName(), constants.font.getValue(), constants.textColor.getValue(), 9, true);
                preview.draw(getWindowX() + (getWidth() - preview.getWidth()) / 2 - 1, getWindowY() + (getHeight() - preview.getHeight()) / 2 - 1);
            }
        });
        add(new SimpleTextComponent(constants, config.getValue()) {
            @Override
            protected void onChange(String text) {
                config.setValue(text);
            }
        });
    }

    @Override
    public Configuration<String> getValue() {
        return config;
    }
}
