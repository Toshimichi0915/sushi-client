package net.toshimichi.sushi.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.utils.EntityInfo;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.combat.DamageUtils;
import net.toshimichi.sushi.utils.player.*;
import net.toshimichi.sushi.utils.world.BlockFace;
import net.toshimichi.sushi.utils.world.BlockPlaceInfo;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class AntiPistonAuraModule extends BaseModule {

    private final List<BlockPlaceInfo> spam = Collections.synchronizedList(new ArrayList<>());
    private final HashSet<PistonInfo> pistons = new HashSet<>();
    private final HashSet<EnderCrystalInfo> crystals = new HashSet<>();
    private final Configuration<IntRange> placeCoolTime;
    private long lastPlaceTick;
    private volatile ItemSlot obsidianSlot;
    private volatile ItemSlot currentSlot;
    private volatile long when;
    private volatile boolean switching;

    public AntiPistonAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        placeCoolTime = provider.get("place_cool_time", "Place Cool Time", null, IntRange.class, new IntRange(20, 1000, 10, 10));
    }

    private boolean updatePlaceCounter() {
        if (System.currentTimeMillis() - lastPlaceTick >= placeCoolTime.getValue().getCurrent()) {
            lastPlaceTick = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        new Thread(() -> {
            while (true) {
                if (!isEnabled()) return;
                try {
                    placeObsidian();
                    long sleep = placeCoolTime.getValue().getCurrent() - (System.currentTimeMillis() - lastPlaceTick);
                    if (sleep > 0) Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    // shut down the thread
                }
            }
        }).start();
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private PistonInfo getPistonInfo(BlockPos pos) {
        synchronized (pistons) {
            for (PistonInfo candidate : pistons) {
                if (candidate.getBlockPos().equals(pos)) return candidate;
            }
        }
        return null;
    }

    private EnderCrystalInfo getNearbyCrystal(Vec3d vec) {
        synchronized (crystals) {
            for (EnderCrystalInfo candidate : crystals) {
                if (candidate.getPos().squareDistanceTo(vec) < 4) return candidate;
            }
        }
        return null;
    }

    private BlockPlaceInfo findBlockPlaceInfo(World world, BlockPos input) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPlaceInfo info = new BlockFace(input.offset(facing), facing.getOpposite()).toBlockPlaceInfo(world);
            BlockPos pos = info.getBlockPos();
            if (BlockUtils.isAir(world, pos.offset(facing))) continue;
            return info;
        }
        return null;
    }

    private void updateAll() {
        synchronized (pistons) {
            pistons.clear();
            forEachNearby(pos -> {
                IBlockState blockState = getWorld().getBlockState(pos);
                if (!(blockState.getBlock() instanceof BlockPistonBase)) return;
                EnumFacing enumFacing = blockState.getValue(BlockDirectional.FACING);
                pistons.add(new PistonInfo(pos, enumFacing));
            });
        }
        synchronized (crystals) {
            crystals.clear();
            for (EntityInfo<EntityEnderCrystal> info : EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityEnderCrystal.class)) {
                if (info.getDistanceSq() > 10) continue;
                EntityEnderCrystal crystal = info.getEntity();
                crystals.add(new EnderCrystalInfo(crystal.getEntityId(), crystal.getPositionVector(), null));
            }
        }
    }

    private synchronized void placeObsidian() {
        if (spam.isEmpty()) return;
        ItemSlot finalObsidianSlot = obsidianSlot;
        if (finalObsidianSlot == null) return;
        if (!updatePlaceCounter()) return;
        switching = true;
        InventoryUtils.moveHotbar(finalObsidianSlot.getIndex());
        for (BlockPlaceInfo info : new ArrayList<>(spam)) {
            if (info == null) continue;
            try (DesyncCloseable closeable = PositionUtils.desync(DesyncMode.LOOK)) {
                BlockUtils.place(info, true);
            }
        }
        InventoryUtils.moveHotbar(currentSlot.getIndex());
        switching = false;
    }

    private void preventPistonAura() {
        forEachNearby(pos -> {
            PistonInfo pistonInfo = getPistonInfo(pos);
            if (pistonInfo == null) return;
            EnumFacing enumFacing = pistonInfo.getFacing();
            EnderCrystalInfo crystal = getNearbyCrystal(BlockUtils.toVec3d(pos.offset(enumFacing)).add(0.5, 0, 0.5));
            if (crystal == null) return;
            Vec3d predicted = crystal.getPos().add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));
            if (DamageUtils.getCrystalDamage(getPlayer(), predicted) < 30) return;
            spam.add(findBlockPlaceInfo(getWorld(), pos));
            if (!BlockUtils.isAir(getWorld(), pos.offset(enumFacing).offset(enumFacing))) {
                spam.add(findBlockPlaceInfo(getWorld(), pos.offset(enumFacing)));
            }
            when = System.currentTimeMillis();
            try (DesyncCloseable closeable = PositionUtils.desync(DesyncMode.ALL)) {
                PositionUtils.move(getPlayer().posX, getPlayer().posY + 0.2, getPlayer().posZ, 0, 0, true, false, DesyncMode.POSITION);
                PositionUtils.lookAt(crystal.getPos(), DesyncMode.LOOK);
                getConnection().sendPacket(crystal.newAttackPacket());
            }
        });
        placeObsidian();
    }

    private void forEachNearby(Consumer<BlockPos> consumer) {
        BlockPos playerPos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
        for (int x = -4; x <= 4; x++) {
            for (int y = 1; y <= 4; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos pos = new BlockPos(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    consumer.accept(pos);
                }
            }
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        updateAll();
        spam.removeIf(it -> {
            if (it == null) return true;
            Block block = getWorld().getBlockState(it.getBlockPos()).getBlock();
            return block != Blocks.PISTON && block != Blocks.PISTON_HEAD && block != Blocks.PISTON_EXTENSION && block != Blocks.AIR ||
                    System.currentTimeMillis() - when > 1000;
        });
        obsidianSlot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), getPlayer(), InventoryType.values());
        ItemSlot finalObsidianSlot = obsidianSlot;
        if (finalObsidianSlot != null && !switching && obsidianSlot.getInventoryType() != InventoryType.HOTBAR) {
            obsidianSlot = InventoryUtils.moveToHotbar(finalObsidianSlot);
        }
        currentSlot = ItemSlot.current();
        preventPistonAura();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketSpawnObject)) return;
        SPacketSpawnObject packet = (SPacketSpawnObject) e.getPacket();
        if (packet.getType() != 51) return;
        Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        synchronized (crystals) {
            crystals.add(new EnderCrystalInfo(packet.getEntityID(), pos, null));
        }
        preventPistonAura();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive2(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketBlockChange)) return;
        SPacketBlockChange packet = (SPacketBlockChange) e.getPacket();
        Block block = packet.getBlockState().getBlock();
        if (block != Blocks.PISTON && block != Blocks.PISTON_HEAD) return;
        if (!(packet.getBlockState().getBlock() instanceof BlockPistonBase)) return;
        EnumFacing enumFacing = packet.getBlockState().getValue(BlockDirectional.FACING);
        synchronized (pistons) {
            pistons.add(new PistonInfo(packet.getBlockPosition(), enumFacing));
        }
        preventPistonAura();
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPacketReceive3(PacketReceiveEvent e) {
        if (!switching) return;
        if (!(e.getPacket() instanceof SPacketHeldItemChange)) return;
        e.setCancelled(true);
    }

    @Override
    public String getDefaultName() {
        return "AntiPistonAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }

    private static class PistonInfo {
        private final BlockPos blockPos;
        private final EnumFacing facing;

        public PistonInfo(BlockPos blockPos, EnumFacing facing) {
            this.blockPos = blockPos;
            this.facing = facing;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public EnumFacing getFacing() {
            return facing;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PistonInfo that = (PistonInfo) o;

            if (blockPos != null ? !blockPos.equals(that.blockPos) : that.blockPos != null) return false;
            return facing == that.facing;
        }

        @Override
        public int hashCode() {
            int result = blockPos != null ? blockPos.hashCode() : 0;
            result = 31 * result + (facing != null ? facing.hashCode() : 0);
            return result;
        }
    }
}
