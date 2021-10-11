package net.sushiclient.client.gui.hud.elements;

import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.hud.BaseHudElementComponent;
import net.sushiclient.client.modules.Module;
import net.sushiclient.client.modules.ModuleSuffix;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;
import net.sushiclient.client.utils.render.TextSettings;

import java.awt.Color;
import java.util.*;

public class ModuleListComponent extends BaseHudElementComponent {

    private final Configuration<EspColor> backgroundColor;
    private final Configuration<IntRange> margin;
    private final Configuration<IntRange> paddingTop;
    private final Configuration<IntRange> paddingBottom;
    private final Configuration<IntRange> paddingLeft;
    private final Configuration<IntRange> paddingRight;

    public ModuleListComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        backgroundColor = getConfiguration("background_color", "Background Color", null, EspColor.class, new EspColor(new Color(0, 0, 0, 100), false, true));
        margin = getConfiguration("margin", "Margin", null, IntRange.class, new IntRange(3, 10, 0, 1));
        paddingTop = getConfiguration("padding_top", "Padding Top", null, IntRange.class, new IntRange(1, 10, 0, 1));
        paddingBottom = getConfiguration("padding_bottom", "Padding Bottom", null, IntRange.class, new IntRange(1, 10, 0, 1));
        paddingLeft = getConfiguration("padding_left", "Padding Left", null, IntRange.class, new IntRange(2, 10, 0, 1));
        paddingRight = getConfiguration("padding_right", "Padding Right", null, IntRange.class, new IntRange(2, 10, 0, 1));
    }

    @Override
    public void onRender() {
        ArrayList<Map.Entry<Module, TextPreview>> list = new ArrayList<>();
        double totalWidth = 0;
        double totalHeight = 0;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!module.isEnabled()) continue;
            if (!module.isVisible()) continue;
            String text = module.getName();
            if (module instanceof ModuleSuffix) text += " §7" + ((ModuleSuffix) module).getSuffix();
            TextPreview preview = GuiUtils.prepareText(text, getTextSettings("text").getValue());
            list.add(new AbstractMap.SimpleEntry<>(module, preview));
            if (totalWidth < preview.getWidth()) totalWidth = preview.getWidth();
            totalHeight += preview.getHeight() + margin.getValue().getCurrent();
        }
        setWidth(totalWidth + paddingLeft.getValue().getCurrent() + paddingRight.getValue().getCurrent());
        setHeight(totalHeight + paddingTop.getValue().getCurrent() + paddingBottom.getValue().getCurrent());

        list.sort(Comparator.comparingDouble(i -> i.getValue().getWidth()));
        if (!getOrigin().isFromBottom()) Collections.reverse(list);

        double height = 0;

        // draw background
        int index = 0;
        for (Map.Entry<Module, TextPreview> entry : list) {
            TextPreview preview = entry.getValue();
            double offset = getOrigin().isFromRight() ? getWidth() - preview.getWidth() - paddingLeft.getValue().getCurrent() - paddingRight.getValue().getCurrent() : 0;
            GuiUtils.drawRect(getWindowX() + offset, getWindowY() + height + (index != 0 ? paddingTop.getValue().getCurrent() : 0),
                    preview.getWidth() + paddingLeft.getValue().getCurrent() + paddingRight.getValue().getCurrent(),
                    preview.getHeight() + margin.getValue().getCurrent() +
                            (index == 0 ? paddingTop.getValue().getCurrent() : 0) +
                            (index == list.size() - 1 ? paddingBottom.getValue().getCurrent() : 0),
                    backgroundColor.getValue().getCurrentColor());
            height += preview.getHeight() + margin.getValue().getCurrent();
            index++;
        }

        height = 0;
        // draw text
        for (Map.Entry<Module, TextPreview> entry : list) {
            TextPreview preview = entry.getValue();
            double offset = getOrigin().isFromRight() ? getWidth() - preview.getWidth() - paddingLeft.getValue().getCurrent() - paddingRight.getValue().getCurrent() : 0;
            preview.draw(getWindowX() + offset + paddingLeft.getValue().getCurrent(), getWindowY() + height + paddingTop.getValue().getCurrent());
            height += preview.getHeight() + margin.getValue().getCurrent();
        }
    }

    @Override
    protected TextSettings getDefaultTextSettings() {
        return super.getDefaultTextSettings()
                .setPts(10)
                .setColor(new EspColor(Color.WHITE, true, true))
                .setFont("sfui");
    }

    @Override
    public String getId() {
        return "modules";
    }

    @Override
    public String getName() {
        return "Module List";
    }
}
