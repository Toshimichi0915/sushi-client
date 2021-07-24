package net.toshimichi.sushi.modules.world;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.mixin.AccessorPlayerControllerMP;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.world.BlockUtils;

public class SpeedMineModule extends BaseModule {

    @Config(id = "delay", name = "Delay")
    public IntRange delay = new IntRange(0, 5, 0, 1);

    @Config(id = "packet_mine", name = "Packet Mine")
    public Boolean packetMine = true;

    @Config(id = "anti_abort", name = "Anti Abort")
    public Boolean antiAbort = true;

    public SpeedMineModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        BlockPos breakingBlock = BlockUtils.getBreakingBlockPos();
        AccessorPlayerControllerMP controller = (AccessorPlayerControllerMP) getController();

        // packet mine
        if (packetMine && breakingBlock != null && !BlockUtils.isAir(getWorld(), breakingBlock)) {
            getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN));
        }

        if (delay.getCurrent() != 5 && ((AccessorPlayerControllerMP) getController()).getBlockHitDelay() == 5) {
            controller.setBlockHitDelay(delay.getCurrent());
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (!antiAbort) return;
        if (!(e.getPacket() instanceof CPacketPlayerDigging)) return;
        CPacketPlayerDigging packet = (CPacketPlayerDigging) e.getPacket();
        if (packet.getAction() != CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) return;
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "SpeedMine";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
