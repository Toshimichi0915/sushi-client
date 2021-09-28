package net.sushiclient.client.modules.world;

import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.mixin.AccessorPlayerControllerMP;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.world.BlockUtils;

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
            sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingBlock, EnumFacing.DOWN));
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
