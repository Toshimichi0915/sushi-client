package net.toshimichi.sushi.modules.player;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.mixin.AccessorMinecraft;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.player.ItemSlot;

public class FastUseModule extends BaseModule {

    @Config(id = "delay", name = "Delay")
    public IntRange delay = new IntRange(0, 10, 1, 1);

    @Config(id = "blocks", name = "Blocks")
    public Boolean blocks = true;

    @Config(id = "items", name = "Items")
    public Boolean items = true;

    @Config(id = "exp_bottle", name = "Exp Bottle", when = "!items")
    public Boolean expBottle = true;

    @Config(id = "crystal", name = "Crystal", when = "!items")
    public Boolean crystal = true;

    @Config(id = "fireworks", name = "Fireworks", when = "!items")
    public Boolean fireworks = true;

    private EnumHand usedHand;
    private int lastDelayTicks;

    public FastUseModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
        if (lastDelayTicks-- > 0) return;
        if (!passItemCheck(ItemSlot.hand(usedHand).getItemStack().getItem())) return;
        lastDelayTicks = delay.getCurrent();
        ((AccessorMinecraft) getClient()).setRightClickDelayTimer(0);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (e.getPacket() instanceof CPacketPlayerTryUseItem) {
            usedHand = ((CPacketPlayerTryUseItem) e.getPacket()).getHand();
        } else if (e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            usedHand = ((CPacketPlayerTryUseItemOnBlock) e.getPacket()).getHand();
        }
    }

    private boolean passItemCheck(Item item) {
        if (item instanceof ItemAir) return false;
        return blocks ||
                expBottle && item == Items.EXPERIENCE_BOTTLE ||
                crystal && item == Items.END_CRYSTAL ||
                fireworks && item == Items.FIREWORKS;
    }

    @Override
    public String getDefaultName() {
        return "FastUse";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.PLAYER;
    }
}
