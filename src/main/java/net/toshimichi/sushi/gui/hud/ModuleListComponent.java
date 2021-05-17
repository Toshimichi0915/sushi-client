package net.toshimichi.sushi.gui.hud;

import net.toshimichi.sushi.Sushi;
import net.toshimichi.sushi.modules.Module;
import net.toshimichi.sushi.utils.GuiUtils;
import net.toshimichi.sushi.utils.TextPreview;

import java.util.*;

public class ModuleListComponent extends BaseHudElementComponent {

    private final HudConstants constants;

    public ModuleListComponent(HudConstants constants) {
        this.constants = constants;
    }

    @Override
    public void onRender() {
        ArrayList<Map.Entry<Module, TextPreview>> list = new ArrayList<>();
        double totalWidth = 0;
        double totalHeight = 0;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!module.isEnabled()) continue;
            TextPreview preview = GuiUtils.prepareText(module.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, true);
            list.add(new AbstractMap.SimpleEntry<>(module, preview));
            totalWidth = preview.getWidth();
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
