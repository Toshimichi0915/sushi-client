package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.AnyPanelComponent;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.ConfigComponent;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.layout.FlowDirection;
import net.sushiclient.client.gui.layout.FlowLayout;
import net.sushiclient.client.gui.theme.Theme;

import java.util.ArrayList;

public class SimpleConfigCategoryComponent extends AnyPanelComponent {

    private final Theme theme;
    private final Configurations configurations;
    private long lastUpdate;

    public SimpleConfigCategoryComponent(Theme theme, Configurations configurations) {
        this.theme = theme;
        this.configurations = configurations;
        setLayout(new FlowLayout(this, FlowDirection.DOWN));
        setMargin(new Insets(2, 2, 2, 2));
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
            component.setMargin(new Insets(0, 0, 0.5, 0));
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