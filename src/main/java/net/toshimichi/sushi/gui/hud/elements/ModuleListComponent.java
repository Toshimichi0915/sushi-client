package net.toshimichi.sushi.gui.hud.elements;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.hud.BaseHudElementComponent;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.render.GuiUtils;
import net.toshimichi.sushi.utils.render.TextPreview;

import java.util.*;

public class ModuleListComponent extends BaseHudElementComponent {

    public ModuleListComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public void onRender() {
        ArrayList<Map.Entry<Module, TextPreview>> list = new ArrayList<>();
        double totalWidth = 0;
        double totalHeight = 0;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!module.isEnabled()) continue;
            TextPreview preview = GuiUtils.prepareText(module.getName(), getTextSettings("text").getValue());
            list.add(new AbstractMap.SimpleEntry<>(module, preview));
            if (totalWidth < preview.getWidth()) totalWidth = preview.getWidth();
            totalHeight += preview.getHeight() + 1;
        }
        setWidth(totalWidth);
        setHeight(totalHeight);

        list.sort(Comparator.comparingDouble(i -> i.getValue().getWidth()));
        if (!getOrigin().isFromBottom()) Collections.reverse(list);

        double height = 0;
        for (Map.Entry<Module, TextPreview> entry : list) {
            TextPreview preview = entry.getValue();
            double width = getOrigin().isFromRight() ? getWidth() - preview.getWidth() : 0;
            preview.draw(getWindowX() + width, getWindowY() + height);
            height += preview.getHeight() + 1;
        }
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
