package net.toshimichi.sushi.modules.world;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Config;
import net.toshimichi.sushi.config.ConfigInjector;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketSendEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.EntityInfo;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.player.ItemSlot;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.HashSet;

public class AntiGhostBlockModule extends BaseModule {

    @Config(id = "crystal_detection", name = "Crystal Detection")
    public Boolean crystalDetection = true;

    @Config(id = "assume_error", name = "Assume Error", when = "crystal_detection")
    public Boolean assumeError = false;

    @Config(id = "place", name = "Place")
    public Boolean placeCheck = true;

    @Config(id = "break", name = "Break")
    public Boolean breakCheck = true;

    private final HashSet<BlockPos> toBeChecked = new HashSet<>();

    public AntiGhostBlockModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
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
        // place check
        if (!toBeChecked.isEmpty()) {
            BlockPos[] arr = toBeChecked.toArray(new BlockPos[0]);
            BlockUtils.checkGhostBlock(arr);
        }
        toBeChecked.clear();

        // crystal detection
        if (crystalDetection) {
            Vec3d p = getPlayer().getPositionVector();
            for (EntityInfo<EntityEnderCrystal> crystal : EntityUtils.getNearbyEntities(p, EntityEnderCrystal.class)) {
                BlockPos pos = BlockUtils.toBlockPos(crystal.getEntity().getPositionVector());
                if (BlockUtils.isAir(getWorld(), pos)) continue;
                if (!EntityUtils.canInteract(crystal.getEntity(), 6, 3))
                    return;
                if (assumeError) getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
                BlockUtils.checkGhostBlock(pos);
            }
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketSend(PacketSendEvent e) {
        if (placeCheck && e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock
                && ItemSlot.current().getItemStack().getItem() instanceof ItemBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) e.getPacket();
            BlockPos blockPos = packet.getPos().add(packet.getFacingX(), packet.getFacingY(), packet.getFacingZ());
            BlockUtils.checkGhostBlock(blockPos);
        }
        if (breakCheck && e.getPacket() instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging) e.getPacket();
            toBeChecked.add(packet.getPosition());
        }
    }

    @Override
    public String getDefaultName() {
        return "AntiGhostBlock";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
