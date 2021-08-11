package net.toshimichi.sushi.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSlotSwitchTask;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.TickUtils;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;
import net.toshimichi.sushi.utils.player.ItemUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

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
                            getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, playerPos, EnumFacing.DOWN));
                            return waitTime;
                        }
                    })
                    .supply(pickaxe)
                    .then(new ItemSlotSwitchTask())
                    .then(() -> getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, playerPos, EnumFacing.DOWN)))
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
