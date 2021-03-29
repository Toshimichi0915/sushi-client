package net.toshimichi.sushi.gui;

import net.minecraft.util.math.MathHelper;
import net.toshimichi.sushi.gui.base.BasePanelComponent;
import net.toshimichi.sushi.utils.GuiUtils;

public class CollapseComponent<T extends Component> extends BasePanelComponent<T> implements FrameComponent<T> {

    private final T component;
    private final CollapseMode mode;
    private int height;
    private double progress;

    public CollapseComponent(T component, CollapseMode mode) {
        this.component = component;
        this.mode = mode;
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
    }

    @Override
    public void onRender() {
        component.setWidth(getWidth());
        GuiUtils.prepareArea(this);
        super.onRender();
        GuiUtils.releaseArea();

        setHeight((int) (progress * component.getHeight()));
        if (mode == CollapseMode.UP)
            component.setY(0);
        else
            component.setY(getHeight() - component.getHeight());
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public T getValue() {
        return component;
    }
}
