package net.sushiclient.client.modules.world;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Config;
import net.sushiclient.client.config.ConfigInjector;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.packet.PacketSendEvent;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityInfo;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.world.BlockUtils;

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
