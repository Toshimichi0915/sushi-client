package net.toshimichi.sushi.modules.combat;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.network.NetworkPlayerInfo;
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
import net.toshimichi.sushi.config.ConfigurationCategory;
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
import net.toshimichi.sushi.utils.player.MovementUtils;
import net.toshimichi.sushi.utils.player.PositionUtils;
import net.toshimichi.sushi.utils.world.BlockUtils;

import java.io.IOException;
import java.util.*;

public class CrystalAuraModule extends BaseModule {

    private static final double WALK_SPEED = 0.0275;

    private final Configuration<DoubleRange> targetRange;
    private final Configuration<DoubleRange> crystalRange;
    private final Configuration<DoubleRange> wallRange;

    private final Configuration<IntRange> placeCoolTime;
    private final Configuration<IntRange> breakCoolTime;
    private final Configuration<IntRange> recalculationCoolTime;

    private final Configuration<DoubleRange> minDamage;
    private final Configuration<DoubleRange> facePlace;
    private final Configuration<IntRange> maxTargets;
    private final Configuration<Boolean> customDamage;
    private final Configuration<IntRange> customPower;
    private final Configuration<DoubleRange> damageRatio;
    private final Configuration<DoubleRange> maxSelfDamage;
    private final Configuration<DoubleRange> minSelfHp;
    private final Configuration<ItemSwitchMode> switchMode;
    private final Configuration<Boolean> y255Attack;

    private final Configuration<DoubleRange> selfPingMultiplier;
    private final Configuration<Boolean> useInputs;
    private final Configuration<Boolean> constantSpeed;
    private final Configuration<DoubleRange> enemyPingMultiplier;
    private final Configuration<IntRange> maxEnemyPing;

    private final Set<EnderCrystalInfo> enderCrystals = new HashSet<>();
    private volatile CrystalAttack crystalAttack;
    private volatile CrystalAttack nearbyCrystalAttack;
    private volatile long lastPlaceTick;
    private volatile long lastBreakTick;
    private volatile long lastRecalculationTick;

    private long counter;

    public CrystalAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        // Range
        ConfigurationCategory range = provider.getCategory("range", "Range Settings", null);
        targetRange = range.get("target_range", "Target Range", null, DoubleRange.class, new DoubleRange(6, 18, 1, 1, 1));
        crystalRange = range.get("crystal_range", "Crystal Range", null, DoubleRange.class, new DoubleRange(6, 10, 1, 0.1, 1));
        wallRange = range.get("wall_range", "Wall Range", null, DoubleRange.class, new DoubleRange(3, 6, 1, 0.1, 1));

        // Cool Time
        ConfigurationCategory coolTime = provider.getCategory("cool_time", "Cool Time Settings", null);
        placeCoolTime = coolTime.get("place_cool_time", "Place Delay", null, IntRange.class, new IntRange(0, 20, 0, 1));
        breakCoolTime = coolTime.get("break_cool_time", "Break Delay", null, IntRange.class, new IntRange(0, 20, 0, 1));
        recalculationCoolTime = coolTime.get("recalculation_cool_time", "Recalculation Delay", null, IntRange.class, new IntRange(5, 20, 1, 1));

        // Damage
        ConfigurationCategory damage = provider.getCategory("damage", "Damage Settings", null);
        minDamage = damage.get("min_damage", "Min Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        facePlace = damage.get("face_place", "Face Place", null, DoubleRange.class, new DoubleRange(5, 20, 0, 0.2, 1));
        maxTargets = damage.get("max_targets", "Max Targets", null, IntRange.class, new IntRange(1, 10, 1, 1));
        customDamage = damage.get("custom_damage", "Custom Damage", null, Boolean.class, false);
        customPower = damage.get("power", "Power", null, IntRange.class, new IntRange(6, 10, 1, 1), customDamage::getValue, false, 0);
        damageRatio = damage.get("damage_ratio", "Damage Ratio", null, DoubleRange.class, new DoubleRange(0.5, 1, 0, 0.05, 2));
        maxSelfDamage = damage.get("max_self_damage", "Max Self Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        minSelfHp = damage.get("min_self_hp", "Min Self HP", null, DoubleRange.class, new DoubleRange(6, 20, 1, 0.1, 1));
        switchMode = damage.get("switch", "Switch Mode", null, ItemSwitchMode.class, ItemSwitchMode.INVENTORY);
        y255Attack = damage.get("y_255_attack", "Y 255 Attack", null, Boolean.class, true);

        // Predict
        ConfigurationCategory predict = provider.getCategory("predict", "Predict Settings", null);
        selfPingMultiplier = predict.get("self_ping_multiplier", "Self Multiplier", null, DoubleRange.class, new DoubleRange(1, 10, 0, 0.1, 1));
        useInputs = predict.get("use_inputs", "Use Inputs", null, Boolean.class, true);
        constantSpeed = predict.get("constant_speed", "Constant Speed", null, Boolean.class, true);
        enemyPingMultiplier = predict.get("enemy_ping_multiplier", "Enemy Multiplier", null, DoubleRange.class, new DoubleRange(1, 10, 0, 0.1, 1));
        maxEnemyPing = predict.get("max_enemy_ping", "Max Enemy Ping", null, IntRange.class, new IntRange(100, 1000, 0, 5));
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

    private Vec3d getPingOffset(EntityPlayer player, boolean self) {
        NetworkPlayerInfo selfInfo = getConnection().getPlayerInfo(getPlayer().getUniqueID());
        NetworkPlayerInfo enemyInfo = getConnection().getPlayerInfo(player.getUniqueID());
        int selfPing = selfInfo == null ? 0 : selfInfo.getResponseTime();
        int enemyPing = enemyInfo == null ? 0 : enemyInfo.getResponseTime();
        Vec3d offset = player.getPositionVector().subtract(new Vec3d(player.prevPosX, player.prevPosY, player.prevPosZ));
        Vec3d result;
        if (self && useInputs.getValue()) result = MovementUtils.getMoveInputs(getPlayer(), true);
        else result = offset.normalize();

        if (!constantSpeed.getValue()) result = result.scale(offset.distanceTo(Vec3d.ZERO) / WALK_SPEED);
        if (self) {
            return result.scale(selfPing / 50D * WALK_SPEED * selfPingMultiplier.getValue().getCurrent());
        } else {
            return result.scale(selfPing / 50D * WALK_SPEED * selfPingMultiplier.getValue().getCurrent() +
                    enemyPing * 50 / WALK_SPEED * enemyPingMultiplier.getValue().getCurrent());
        }
    }

    private double getDamage(Vec3d pos, EntityPlayer player, Vec3d offset) {
        double power = customDamage.getValue() ? customPower.getValue().getCurrent() : 6;
        double damage = DamageUtils.getExplosionDamage(player, offset, pos, power);
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
            damages.add(new AbstractMap.SimpleEntry<>(player, getDamage(pos, player, getPingOffset(player, false))));
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

        double selfDamage = getDamage(crystalPos, getPlayer(), getPingOffset(getPlayer(), true));
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
        if (counter - lastRecalculationTick >= recalculationCoolTime.getValue().getCurrent()) {
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
            if (y255Attack.getValue()) {
                getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(BlockUtils.toBlockPos(crystalPos).add(0, -1, 0),
                        EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.5F, 0, 0.5F));
            } else {
                getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(BlockUtils.toBlockPos(crystalPos),
                        EnumFacing.UP, EnumHand.MAIN_HAND, 0.5F, 1, 0.5F));
            }
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
