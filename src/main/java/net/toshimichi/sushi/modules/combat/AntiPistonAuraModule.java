package net.toshimichi.sushi.modules.combat;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.BlockPos;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.BlockPlaceTask;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.*;

import java.util.ArrayList;
import java.util.List;

public class AntiPistonAuraModule extends BaseModule {

    private List<PistonAuraAttack> attacks = new ArrayList<>();
    private boolean running;

    public AntiPistonAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
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
        if (running) return;
        attacks = PistonAuraUtils.find(getPlayer(), getPlayer());
        attacks.removeIf(it -> it.getCrystal() == null);
        for (PistonAuraAttack attack : attacks) {
            BlockPos pos1 = attack.getCrystalPos();
            BlockPos pos2 = attack.getPistonPos();
            BlockPlaceInfo info1 = BlockUtils.findBlockPlaceInfo(getWorld(), pos1);
            BlockPlaceInfo info2 = BlockUtils.findBlockPlaceInfo(getWorld(), pos2);
            ArrayList<BlockPlaceInfo> placed = new ArrayList<>();
            if (info1 != null) placed.add(info1);
            if (info2 != null) placed.add(info2);
            if (placed.isEmpty()) return;
            PositionUtils.desync(DesyncMode.LOOK);
            PositionUtils.lookAt(attack.getCrystal().getPositionVector(), DesyncMode.LOOK);
            PositionUtils.pop();
            getConnection().sendPacket(new CPacketUseEntity(attack.getCrystal()));
            TaskExecutor.newTaskChain()
                    .supply(() -> Item.getItemFromBlock(Blocks.OBSIDIAN))
                    .then(new ItemSwitchTask(null, true))
                    .abortIf(found -> {
                        if (!found) running = false;
                        return !found;
                    }).supply(() -> placed)
                    .then(new BlockPlaceTask(DesyncMode.LOOK))
                    .then(() -> running = false)
                    .execute(true);
        }
    }

    @Override
    public String getDefaultName() {
        return "AntiPistonAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
