package net.toshimichi.sushi.gui;

public class SmoothCollapseComponent<T extends Component> extends CollapseComponent<T> {

    private final double collapseSpeed;
    private boolean collapsed;

    public SmoothCollapseComponent(T component, CollapseMode mode, double collapseSpeed) {
        super(component, mode);
        this.collapseSpeed = collapseSpeed;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    @Override
    public void onRelocate() {
        double progress;
        if (collapsed) progress = getProgress() + collapseSpeed;
        else progress = getProgress() - collapseSpeed;
        setProgress(progress);
        super.onRelocate();
    }
}
