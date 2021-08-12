package net.sushiclient.client.gui;

import net.minecraft.util.math.MathHelper;

public class SmoothCollapseComponent<T extends Component> extends CollapseComponent<T> {

    private final double totalMillis;
    private long millis;
    private boolean collapsed = true;

    public SmoothCollapseComponent(T component, CollapseMode mode, double totalMillis) {
        super(component, mode);
        this.totalMillis = totalMillis;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.millis = System.currentTimeMillis();
        this.collapsed = collapsed;
    }

    @Override
    public void onRelocate() {
        double progress = MathHelper.clamp((System.currentTimeMillis() - millis) / totalMillis, 0, 1);
        if (!collapsed) setProgress(progress);
        else setProgress(1 - progress);
        super.onRelocate();
    }
}
