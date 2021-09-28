package net.sushiclient.client.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.ItemSlotSwitchTask;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.TickUtils;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.player.ItemUtils;
import net.sushiclient.client.utils.world.BlockUtils;

public class AnvilMinerModule extends BaseModule {

    private boolean running;

    public AnvilMinerModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        running = false;
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (running) return;
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(7)) {
            BlockPos playerPos = BlockUtils.toBlockPos(player.getPositionVector());
            if (getWorld().getBlockState(playerPos).getBlock() != Blocks.ANVIL) continue;
            if (!BlockUtils.canInteract(playerPos)) continue;
            ItemSlot pickaxe = InventoryUtils.findBestTool(true, false, getWorld().getBlockState(playerPos));
            if (pickaxe.getItemStack().getItem() != Items.DIAMOND_PICKAXE) continue;
            running = true;
            TaskExecutor.newTaskChain()
                    .delay(() -> {
                        int waitTime = ItemUtils.getDestroyTime(playerPos, pickaxe.getItemStack());
                        if (playerPos.equals(BlockUtils.getBreakingBlockPos())) {
                            return Math.max(waitTime - (TickUtils.current() - BlockUtils.getBreakingTime()), 0);
                        } else {
                            sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, playerPos, EnumFacing.DOWN));
                            return waitTime;
                        }
                    })
                    .supply(pickaxe)
                    .then(new ItemSlotSwitchTask())
                    .then(() -> sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, playerPos, EnumFacing.DOWN)))
                    .last(() -> running = false)
                    .execute();
            return;
        }
    }

    @Override
    public String getDefaultName() {
        return "AnvilMiner";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
