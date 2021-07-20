package net.toshimichi.sushi.events.render;

import net.minecraft.item.ItemStack;
import net.toshimichi.sushi.events.CancellableEvent;

public class ItemActivationRenderEvent extends CancellableEvent {
    private final ItemStack itemStack;

    public ItemActivationRenderEvent(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
