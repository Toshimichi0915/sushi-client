package net.toshimichi.sushi.modules.player;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.player.InventoryType;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;

public class RefillModule extends BaseModule {

    @Config(id = "exp_bottle", name = "Exp Bottle")
    public Boolean expBottle = true;

    @Config(id = "crystal", name = "Crystal")
    public Boolean crystal = true;

    @Config(id = "gapple", name = "Gapple")
    public Boolean gapple = true;

    @Config(id = "piston", name = "Piston")
    public Boolean piston = true;

    @Config(id = "redstone_block", name = "Redstone Block")
    public Boolean redstoneBlock = true;

    @Config(id = "redstone_touch", name = "Redstone Touch")
    public Boolean redstoneTouch = true;

    @Config(id = "block", name = "Block")
    public Boolean block = false;

    @Config(id = "threshold", name = "Threshold")
    public IntRange threshold = new IntRange(10, 63, 1, 1);

    public RefillModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        new ConfigInjector(provider).inject(this);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        for (ItemSlot hotbar : InventoryType.HOTBAR) {
            Item item = hotbar.getItemStack().getItem();
            if (hotbar.getItemStack().getCount() > threshold.getCurrent()) continue;
            ItemStack item1 = hotbar.getItemStack().copy();
            item1.setCount(1);

            ItemSlot from = null;
            for (ItemSlot candidate : InventoryType.MAIN) {
                ItemStack item2 = candidate.getItemStack().copy();
                item2.setCount(1);
                if (ItemStack.areItemStacksEqual(item1, item2)) {
                    from = candidate;
                    break;
                }
            }

            if (from == null) continue;
            if (item == Items.EXPERIENCE_BOTTLE && expBottle ||
                    item == Items.END_CRYSTAL && crystal ||
                    item == Items.GOLDEN_APPLE && gapple ||
                    item instanceof ItemBlock && block ||
                    item == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK) && redstoneBlock ||
                    item == Item.getItemFromBlock(Blocks.REDSTONE_TORCH) && redstoneTouch ||
                    item == Item.getItemFromBlock(Blocks.PISTON) && piston) {
                InventoryUtils.moveTo(from, hotbar);
            }
        }
    }

    @Override
    public String getDefaultName() {
        return "Refill";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
