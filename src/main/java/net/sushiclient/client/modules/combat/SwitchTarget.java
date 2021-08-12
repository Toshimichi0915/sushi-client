package net.sushiclient.client.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.sushiclient.client.config.data.Named;

public enum SwitchTarget implements Named {
    GAPPLE("Gapple", Items.GOLDEN_APPLE),
    TOTEM("Totem", Items.TOTEM_OF_UNDYING),
    CRYSTAL("Crystal", Items.END_CRYSTAL);

    private final String name;
    private final Item item;

    SwitchTarget(String name, Item item) {
        this.name = name;
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String getName() {
        return name;
    }
}
