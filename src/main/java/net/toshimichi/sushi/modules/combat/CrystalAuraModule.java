package net.toshimichi.sushi.modules.combat;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.packet.PacketReceiveEvent;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSwitchMode;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.combat.DamageUtils;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.io.IOException;
import java.util.*;

public class CrystalAuraModule extends BaseModule {

    private final Configuration<DoubleRange> targetRange;
    private final Configuration<DoubleRange> crystalRange;
    private final Configuration<DoubleRange> wallRange;
    private final Configuration<ItemSwitchMode> switchMode;
    private final Configuration<IntRange> placeCoolTime;
    private final Configuration<IntRange> breakCoolTime;
    private final Configuration<IntRange> recalculationTime;
    private final Configuration<DoubleRange> minDamage;
    private final Configuration<DoubleRange> facePlace;
    private final Configuration<IntRange> maxTargets;
    private final Configuration<Boolean> customDamage;
    private final Configuration<IntRange> customPower;
    private final Configuration<DoubleRange> damageRatio;
    private final Configuration<DoubleRange> maxSelfDamage;
    private final Configuration<DoubleRange> minSelfHp;
    private final Set<EnderCrystalInfo> enderCrystals = new HashSet<>();

    private volatile CrystalAttack crystalAttack;
    private volatile CrystalAttack nearbyCrystalAttack;
    private volatile long lastPlaceTick;
    private volatile long lastBreakTick;
    private volatile long lastRecalculationTick;

    private long counter;

    public CrystalAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        targetRange = provider.get("target_range", "Target Range", null, DoubleRange.class, new DoubleRange(6, 18, 1, 1, 1));
        crystalRange = provider.get("crystal_range", "Crystal Range", null, DoubleRange.class, new DoubleRange(6, 10, 1, 0.1, 1));
        wallRange = provider.get("wall_range", "Wall Range", null, DoubleRange.class, new DoubleRange(3, 6, 1, 0.1, 1));
        switchMode = provider.get("switch", "Switch Mode", null, ItemSwitchMode.class, ItemSwitchMode.INVENTORY);
        placeCoolTime = provider.get("place_cool_time", "Place Cool Time", null, IntRange.class, new IntRange(0, 20, 0, 1));
        breakCoolTime = provider.get("break_cool_time", "Break Cool Time", null, IntRange.class, new IntRange(0, 20, 0, 1));
        recalculationTime = provider.get("recalculation_time", "Recalculation Time", null, IntRange.class, new IntRange(5, 20, 1, 1));
        minDamage = provider.get("min_damage", "Min Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        facePlace = provider.get("face_place", "Face Place", null, DoubleRange.class, new DoubleRange(5, 20, 0, 0.2, 1));
        maxTargets = provider.get("max_targets", "Max Targets", null, IntRange.class, new IntRange(1, 10, 1, 1));
        customDamage = provider.get("custom_damage", "Custom Damage", null, Boolean.class, false);
        customPower = provider.get("power", "Power", null, IntRange.class, new IntRange(6, 10, 1, 1), customDamage::getValue, false, 0);
        damageRatio = provider.get("damage_ratio", "Damage Ratio", null, DoubleRange.class, new DoubleRange(0.5, 1, 0, 0.05, 2));
        maxSelfDamage = provider.get("max_self_damage", "Max Self Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        minSelfHp = provider.get("min_self_hp", "Min Self HP", null, DoubleRange.class, new DoubleRange(6, 20, 1, 0.1, 1));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        crystalAttack = null;
        nearbyCrystalAttack = null;
        lastBreakTick = 0;
        lastPlaceTick = 0;
        lastRecalculationTick = 0;
        counter = 100;
    }

    private double getDamage(Vec3d pos, EntityPlayer player) {
        double power = customDamage.getValue() ? customPower.getValue().getCurrent() : 6;
        double damage = DamageUtils.getExplosionDamage(player, pos, power);
        return DamageUtils.applyModifier(player, damage, DamageUtils.EXPLOSION);
    }

    private CrystalAttack getCrystalAttack(int crystal, Vec3d pos, AxisAlignedBB box) {
        ArrayList<Map.Entry<EntityPlayer, Double>> damages = new ArrayList<>();
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            double range = targetRange.getValue().getCurrent();
            if (getPlayer().getDistanceSq(entity) > range * range) continue;
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().equals(getPlayer().getName())) continue;
            damages.add(new AbstractMap.SimpleEntry<>(player, getDamage(pos, player)));
        }
        if (damages.isEmpty()) return null;
        // sort
        damages.sort(Comparator.comparingDouble(Map.Entry::getValue));
        Collections.reverse(damages);
        LinkedHashMap<EntityPlayer, Double> sortedMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<EntityPlayer, Double> entry : damages) {
            if (index++ >= maxTargets.getValue().getCurrent()) break;
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return new CrystalAttack(crystal, pos, box, sortedMap);
    }

    private boolean checkFacePlace(CrystalAttack attack) {
        for (EntityPlayer player : attack.damages.keySet()) {
            if (player.getHealth() <= facePlace.getValue().getCurrent()) return true;
        }
        return false;
    }

    private boolean filter(CrystalAttack attack, boolean checkCollision) {
        if (attack == null) return false;
        Vec3d crystalPos = attack.info.pos;
        AxisAlignedBB crystalBox = attack.info.box;

        List<Entity> entities = getWorld().getEntitiesWithinAABBExcludingEntity(null, crystalBox);
        entities.removeIf(p -> p instanceof EntityEnderCrystal);
        if (checkCollision && !entities.isEmpty()) return false;

        double selfDamage = getDamage(crystalPos, getPlayer());
        double ratio = selfDamage / attack.getTotalDamage();
        if (attack.getTotalDamage() < minDamage.getValue().getCurrent() &&
                (attack.damages.isEmpty() || attack.getTotalDamage() <= 2 || !checkFacePlace(attack))) {
            return false;
        }
        if (selfDamage > maxSelfDamage.getValue().getCurrent()) return false;
        if (ratio > damageRatio.getValue().getCurrent() && !checkFacePlace(attack)) return false;
        if (getPlayer().getHealth() - selfDamage < minSelfHp.getValue().getCurrent()) return false;
        return true;
    }

    private CrystalAttack findBestCrystalAttack(List<CrystalAttack> attacks) {
        CrystalAttack best = null;
        double maxDamage = 0;
        for (CrystalAttack attack : attacks) {
            double damage = attack.getTotalDamage();
            if (damage > maxDamage) {
                maxDamage = damage;
                best = attack;
            }
        }
        return best;
    }

    private void refreshEnderCrystals() {
        synchronized (enderCrystals) {
            enderCrystals.clear();
            for (Entity enderCrystal : getWorld().loadedEntityList) {
                if (!(enderCrystal instanceof EntityEnderCrystal)) continue;
                enderCrystals.add(new EnderCrystalInfo(enderCrystal.getEntityId(), enderCrystal.getPositionVector(), enderCrystal.getEntityBoundingBox()));
            }
        }
    }

    private void refreshCrystalAttack() {
        int distance = (int) Math.ceil(crystalRange.getValue().getCurrent());

        // refresh possible crystal placements
        ArrayList<CrystalAttack> attacks = new ArrayList<>();
        for (int x = -distance; x < distance; x++) {
            for (int y = -distance; y < distance; y++) {
                for (int z = -distance; z < distance; z++) {
                    if (x * x + y * y + z * z > distance * distance) continue;
                    BlockPos pos = new BlockPos(x + getPlayer().posX, y + getPlayer().posY, z + getPlayer().posZ);
                    Vec3d vec = BlockUtils.toVec3d(pos).add(0.5, 1, 0.5);

                    // check whether the block is obsidian/bedrock
                    IBlockState blockState = getWorld().getBlockState(pos);
                    Block block = blockState.getBlock();
                    if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) continue;

                    // check distance
                    if (!EntityUtils.canInteract(vec, crystalRange.getValue().getCurrent(), wallRange.getValue().getCurrent()))
                        continue;

                    // check collisions
                    pos = pos.add(0, 1, 0);
                    AxisAlignedBB crystal = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                            pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
                    if (getWorld().collidesWithAnyBlock(crystal)) continue;

                    CrystalAttack attack = getCrystalAttack(-1, vec, crystal);
                    if (filter(attack, true)) attacks.add(attack);
                }
            }
        }

        crystalAttack = findBestCrystalAttack(attacks);

        // nearby crystals
        ArrayList<CrystalAttack> nearby = new ArrayList<>();
        synchronized (enderCrystals) {
            for (EnderCrystalInfo entity : enderCrystals) {
                double distanceSq = getPlayer().getPositionVector().squareDistanceTo(entity.pos);
                if (distanceSq > crystalRange.getValue().getCurrent() * crystalRange.getValue().getCurrent()) continue;
                if (crystalAttack != null && crystalAttack.info.box.intersects(entity.box)) continue;
                CrystalAttack attack = getCrystalAttack(entity.entityId, entity.pos, entity.box);
                if (filter(attack, false)) nearby.add(attack);
            }
        }

        nearbyCrystalAttack = findBestCrystalAttack(nearby);
    }

    private EnderCrystalInfo getCollidingEnderCrystal(AxisAlignedBB box) {
        synchronized (enderCrystals) {
            for (EnderCrystalInfo enderCrystalInfo : enderCrystals) {
                if (enderCrystalInfo.box.intersects(box)) return enderCrystalInfo;
            }
        }
        return null;
    }

    private boolean updateBreakCounter() {
        if (counter - lastBreakTick >= breakCoolTime.getValue().getCurrent()) {
            lastBreakTick = counter;
            return true;
        }
        return false;
    }

    private boolean updatePlaceCounter() {
        if (counter - lastPlaceTick >= placeCoolTime.getValue().getCurrent()) {
            lastPlaceTick = counter;
            return true;
        }
        return false;
    }

    private boolean updateRecalculationCounter() {
        if (counter - lastRecalculationTick >= recalculationTime.getValue().getCurrent()) {
            lastRecalculationTick = counter;
            return true;
        }
        return false;
    }

    private void breakEnderCrystal(EnderCrystalInfo enderCrystal) {
        PositionUtils.desync(DesyncMode.LOOK);
        PositionUtils.lookAt(enderCrystal.pos, DesyncMode.LOOK);
        CPacketUseEntity packet = new CPacketUseEntity();
        PacketBuffer write = new PacketBuffer(Unpooled.buffer());
        write.writeVarInt(enderCrystal.entityId);
        write.writeEnumValue(CPacketUseEntity.Action.ATTACK);
        try {
            packet.readPacketData(write);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConnection().sendPacket(packet);
        PositionUtils.pop();
    }

    private synchronized void executeAttack() {

        // break
        if (nearbyCrystalAttack != null && updateBreakCounter()) breakEnderCrystal(nearbyCrystalAttack.info);

        if (crystalAttack == null) return;
        Vec3d crystalPos = crystalAttack.info.pos;
        if (updatePlaceCounter()) {
            EnderCrystalInfo colliding = getCollidingEnderCrystal(crystalAttack.info.box);
            if (colliding != null && updateBreakCounter()) breakEnderCrystal(colliding);
            lastPlaceTick = counter;
            PositionUtils.desync(DesyncMode.LOOK);
            PositionUtils.lookAt(crystalPos, DesyncMode.LOOK);
            PositionUtils.pop();
            getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(BlockUtils.toBlockPos(crystalPos).add(0, -1, 0),
                    EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
        }

        nearbyCrystalAttack = null;
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        counter++;
        refreshEnderCrystals();
        if (updateRecalculationCounter()) refreshCrystalAttack();
        if (crystalAttack == null && nearbyCrystalAttack == null) return;
        TaskExecutor.newTaskChain()
                .supply(() -> Items.END_CRYSTAL)
                .then(new ItemSwitchTask(null, switchMode.getValue()))
                .execute();

        executeAttack();
    }

    @EventHandler(timing = EventTiming.PRE, priority = 1000)
    public void onPacketReceive(PacketReceiveEvent e) {
        if (!(e.getPacket() instanceof SPacketSpawnObject)) return;
        SPacketSpawnObject packet = (SPacketSpawnObject) e.getPacket();
        if (packet.getType() != 51) return;
        synchronized (enderCrystals) {
            Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
            AxisAlignedBB box = new AxisAlignedBB(pos.x - 0.75, pos.y, pos.z - 0.75, pos.x + 0.75, pos.y + 1.5, pos.z + 0.75);
            enderCrystals.add(new EnderCrystalInfo(packet.getEntityID(), pos, box));
        }
        executeAttack();
    }

    @Override
    public String getDefaultName() {
        return "CrystalAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }

    private static class CrystalAttack {
        EnderCrystalInfo info;
        LinkedHashMap<EntityPlayer, Double> damages;
        double cachedTotalDamage = -1;

        CrystalAttack(int entity, Vec3d crystalPos, AxisAlignedBB box, LinkedHashMap<EntityPlayer, Double> damages) {
            this.info = new EnderCrystalInfo(entity, crystalPos, box);
            this.damages = damages;
        }

        double getTotalDamage() {
            if (cachedTotalDamage != -1) return cachedTotalDamage;
            double total = 0;
            for (double damage : damages.values()) total += damage;
            cachedTotalDamage = total;
            return total;
        }
    }

    private static class EnderCrystalInfo {
        final int entityId;
        final Vec3d pos;
        final AxisAlignedBB box;

        public EnderCrystalInfo(int entityId, Vec3d pos, AxisAlignedBB box) {
            this.entityId = entityId;
            this.pos = pos;
            this.box = box;
        }
    }
}
