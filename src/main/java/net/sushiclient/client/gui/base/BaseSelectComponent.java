package net.sushiclient.client.gui.base;

import net.sushiclient.client.gui.Anchor;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.Origin;
import net.sushiclient.client.gui.SelectComponent;

import java.util.ArrayList;

abstract public class BaseSelectComponent<T> extends BaseListComponent<T> implements SelectComponent<T> {

    private int maxSelect;

    public BaseSelectComponent(ArrayList<T> internal, int maxSelect) {
        super(internal);
        this.maxSelect = maxSelect;
    }

    public BaseSelectComponent(int x, int y, int width, int height, Anchor anchor, Origin origin, Component parent, ArrayList<T> internal, int maxSelect) {
        super(x, y, width, height, anchor, origin, parent, internal);
        this.maxSelect = maxSelect;
    }

    @Override
    public int getMaxSelect() {
        return maxSelect;
    }

    @Override
    public void setMaxSelect(int max) {
        this.maxSelect = max;
    }
}
