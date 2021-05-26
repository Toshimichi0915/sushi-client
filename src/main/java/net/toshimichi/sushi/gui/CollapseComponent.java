package net.toshimichi.sushi.gui;

import net.minecraft.util.math.MathHelper;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.gui.layout.EmptyLayout;
import net.toshimichi.sushi.utils.GuiUtils;

public class CollapseComponent<T extends Component> extends BasePanelComponent<T> implements FrameComponent<T> {

    private final T component;
    private final CollapseMode mode;
    private double height;
    private double progress;

    public CollapseComponent(T component, CollapseMode mode) {
        this.component = component;
        this.mode = mode;
        setLayout(new EmptyLayout(this));
        add(component);
    }

    public CollapseMode getMode() {
        return mode;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = MathHelper.clamp(progress, 0, 1);
        component.setVisible(this.progress != 0);
    }

    @Override
    public void onRender() {
        GuiUtils.prepareArea(this);
        super.onRender();
        GuiUtils.releaseArea();
    }

    @Override
    public void onRelocate() {
        super.onRelocate();
        Insets margin = component.getMargin();
        component.setWidth(getWidth() - margin.getLeft() - margin.getRight());
        component.setX(component.getMargin().getLeft());
        setHeight(progress * (component.getHeight() + margin.getTop() + margin.getBottom()));
        if (mode == CollapseMode.UP)
            component.setY(margin.getTop());
        else
            component.setY(getHeight() - component.getHeight() - margin.getLeft() - margin.getBottom());
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public T getValue() {
        return component;
    }

    @Override
    public Insets getFrame() {
        return getMargin();
    }
}
