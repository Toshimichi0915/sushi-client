package net.toshimichi.sushi.gui.theme.simple;

import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.gui.Component;
import net.toshimichi.sushi.gui.ConfigComponent;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.FlowDirection;
import net.toshimichi.sushi.gui.layout.FlowLayout;
import net.toshimichi.sushi.gui.theme.Theme;

import java.util.ArrayList;

public class SimpleConfigCategoryComponent extends BasePanelComponent<Component> {

    private final Theme theme;
    private final Configurations configurations;
    private long lastUpdate;

    public SimpleConfigCategoryComponent(Theme theme, Configurations configurations) {
        this.theme = theme;
        this.configurations = configurations;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
    }

    private boolean contains(Configuration<?> conf) {
        for (Component component : this) {
            if (!(component instanceof ConfigComponent)) continue;
            if (((ConfigComponent<?>) component).getValue().equals(conf)) return true;
        }
        return false;
    }

    private void update() {
        for (Configuration<?> conf : configurations.getAll()) {
            if (!conf.isValid()) continue;
            if (contains(conf)) continue;
            ConfigComponent<?> component = theme.newConfigComponent(conf);
            if (component == null) continue;
            add(component, true);
        }
        for (Component component : new ArrayList<>(this)) {
            if (!(component instanceof ConfigComponent)) continue;
            if (((ConfigComponent<?>) component).getValue().isValid()) continue;
            remove(component);
        }

        sort((a, b) -> {
            int aPriority = 1000000;
            int bPriority = 1000000;
            if (a instanceof ConfigComponent)
                aPriority = ((ConfigComponent<?>) a).getValue().getPriority();
            if (b instanceof ConfigComponent)
                bPriority = ((ConfigComponent<?>) b).getValue().getPriority();
            return Integer.compare(aPriority, bPriority);
        });
    }

    @Override
    public void onRelocate() {
        if (System.currentTimeMillis() - lastUpdate > 1000) {
            lastUpdate = System.currentTimeMillis();
            update();
        }
        super.onRelocate();
    }
}