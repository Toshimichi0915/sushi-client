package net.toshimichi.sushi.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.Configurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.config.data.IntRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSwitchMode;
import net.toshimichi.sushi.task.tasks.ItemSwitchTask;
import net.toshimichi.sushi.utils.DamageUtils;
import net.toshimichi.sushi.utils.DesyncMode;
import net.toshimichi.sushi.utils.PositionUtils;

import java.util.*;

public class CrystalAuraModule extends BaseModule {

    private final Configuration<DoubleRange> range;
    private final Configuration<ItemSwitchMode> switchMode;
    private final Configuration<IntRange> placeCoolTime;
    private final Configuration<IntRange> breakCoolTime;
    private final Configuration<DoubleRange> minDamage;
    private final Configuration<IntRange> maxTargets;
    private final Configuration<Boolean> customDamage;
    private final Configuration<IntRange> customPower;
    private final Configuration<DoubleRange> damageRatio;
    private final Configuration<DoubleRange> maxSelfDamage;
    private final Configuration<Boolean> avoidSuicide;
    private long lastPlaceTick;
    private long lastBreakTick;
    private long counter;

    public CrystalAuraModule(String id, Modules modules, Categories categories, Configurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        range = provider.get("range", "Range", null, DoubleRange.class, new DoubleRange(4.5, 10, 1, 0.1, 1));
        switchMode = provider.get("switch", "Switch Mode", null, ItemSwitchMode.class, ItemSwitchMode.INVENTORY);
        placeCoolTime = provider.get("place_cool_time", "Place Cool Time", null, IntRange.class, new IntRange(1, 20, 0, 1));
        breakCoolTime = provider.get("break_cool_time", "Break Cool Time", null, IntRange.class, new IntRange(1, 20, 0, 1));
        minDamage = provider.get("min_damage", "Min Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        maxTargets = provider.get("max_targets", "Max Targets", null, IntRange.class, new IntRange(1, 10, 1, 1));
        customDamage = provider.get("custom_damage", "Custom Damage", null, Boolean.class, false);
        customPower = provider.get("power", "Power", null, IntRange.class, new IntRange(6, 10, 1, 1), customDamage::getValue, null, false, 0);
        damageRatio = provider.get("damage_ratio", "Damage Ratio", null, DoubleRange.class, new DoubleRange(0.5, 1, 0, 0.05, 2));
        maxSelfDamage = provider.get("max_self_damage", "Max Self Damage", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.2, 1));
        avoidSuicide = provider.get("avoid_suicide", "Avoid Suicide", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    private AxisAlignedBB getSpawnEntityBox(BlockPos pos) {
        return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }

    private double getDamage(BlockPos pos, EntityPlayer player) {
        double power = customDamage.getValue() ? customPower.getValue().getCurrent() : 6;
        double damage = DamageUtils.getExplosionDamage(player, new Vec3d(pos.getX(), pos.getY(), pos.getZ()), power);
        return DamageUtils.applyModifier(player, damage, DamageSource.FIREWORKS);
    }

    private CrystalAttack getCrystalAttack(BlockPos pos, AxisAlignedBB box) {
        ArrayList<Map.Entry<EntityPlayer, Double>> damages = new ArrayList<>();
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityPlayer)) continue;
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getName().equals(getPlayer().getName())) continue;
            damages.add(new AbstractMap.SimpleEntry<>(player, getDamage(pos, player)));
        }

        // sort
        damages.sort(Comparator.comparingDouble(Map.Entry::getValue));
        Collections.reverse(damages);
        LinkedHashMap<EntityPlayer, Double> sortedMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<EntityPlayer, Double> entry : damages) {
            if (index++ >= maxTargets.getValue().getCurrent()) break;
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return new CrystalAttack(pos, box, sortedMap);
    }

    private boolean filter(CrystalAttack attack) {
        BlockPos crystalPos = attack.crystalPos;
        AxisAlignedBB crystalBox = attack.crystalBox;

        List<Entity> entities = getWorld().getEntitiesWithinAABBExcludingEntity(null, crystalBox);
        entities.removeIf(p -> p instanceof EntityEnderCrystal);
        if (!entities.isEmpty()) return false;

        double selfDamage = getDamage(crystalPos, getPlayer());
        double ratio = selfDamage / attack.getTotalDamage();
        if (attack.getTotalDamage() < minDamage.getValue().getCurrent()) return false;
        if (selfDamage > maxSelfDamage.getValue().getCurrent()) return false;
        if (ratio > damageRatio.getValue().getCurrent()) return false;
        if (avoidSuicide.getValue() && selfDamage > getPlayer().getHealth()) return false;
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

    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        counter++;

        Block bedrock = Block.getBlockById(7);
        Block obsidian = Block.getBlockById(49);

        int distance = (int) Math.ceil(range.getValue().getCurrent());

        // refresh possible crystal placements
        ArrayList<CrystalAttack> attacks = new ArrayList<>();
        for (int x = -distance; x < distance; x++) {
            for (int y = -distance; y < distance; y++) {
                for (int z = -distance; z < distance; z++) {
                    BlockPos pos = new BlockPos(x + (int) getPlayer().posX, y + (int) getPlayer().posY, z + (int) getPlayer().posZ);

                    // check distance
                    double distanceSq = getPlayer().getPositionVector().squareDistanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
                    if (distanceSq > range.getValue().getCurrent() * range.getValue().getCurrent()) continue;

                    // check whether the block is obsidian/bedrock
                    IBlockState blockState = getWorld().getBlockState(pos);
                    Block block = blockState.getBlock();
                    if (block != bedrock && block != obsidian) continue;

                    // check collisions
                    pos = pos.add(0, 1, 0);
                    AxisAlignedBB crystal = getSpawnEntityBox(pos);
                    if (getWorld().collidesWithAnyBlock(crystal)) continue;

                    CrystalAttack attack = getCrystalAttack(pos, crystal);
                    if (filter(attack)) attacks.add(attack);
                }
            }
        }

        // nearby crystals
        ArrayList<CrystalAttack> nearby = new ArrayList<>();
        for (Entity entity : getWorld().loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            BlockPos pos = new BlockPos((int) entity.posX, (int) entity.posY, (int) entity.posZ);
            double distanceSq = getPlayer().getPositionVector().squareDistanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
            if (distanceSq > range.getValue().getCurrent() * range.getValue().getCurrent()) continue;
            CrystalAttack attack = getCrystalAttack(pos, entity.getEntityBoundingBox());
            if (filter(attack)) nearby.add(attack);
        }

        CrystalAttack best;
        if (nearby.isEmpty()) best = findBestCrystalAttack(attacks);
        else best = findBestCrystalAttack(nearby);
        if (best == null) return;

        BlockPos crystalPos = best.crystalPos;
        AxisAlignedBB crystalBox = best.crystalBox;

        // place/break end crystal
        if (crystalPos == null) return;
        List<Entity> entities = getWorld().getEntitiesWithinAABBExcludingEntity(null, crystalBox);
        entities.removeIf(p -> !(p instanceof EntityEnderCrystal));
        Vec3d lookAt;
        Entity interact = null;
        if (entities.isEmpty()) {
            lookAt = new Vec3d(crystalPos.getX(), crystalPos.getY(), crystalPos.getZ());
        } else {
            interact = entities.get(0);
            lookAt = interact.getPositionVector();
        }
        if (interact != null) {
            if (counter - lastBreakTick < breakCoolTime.getValue().getCurrent()) return;
            lastBreakTick = counter;
            PositionUtils.desync(DesyncMode.LOOK);
            PositionUtils.lookAt(lookAt, DesyncMode.LOOK);
            getConnection().sendPacket(new CPacketUseEntity(interact));
            PositionUtils.pop();
        }

        if (counter - lastPlaceTick < placeCoolTime.getValue().getCurrent()) return;
        lastPlaceTick = counter;
        TaskExecutor.newTaskChain()
                .supply(() -> Item.getItemById(426))
                .then(new ItemSwitchTask(null, switchMode.getValue()))
                .abortIf(found -> !found)
                .then(() -> {
                    PositionUtils.desync(DesyncMode.LOOK);
                    PositionUtils.lookAt(lookAt, DesyncMode.LOOK);
                    PositionUtils.pop();
                    getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(crystalPos.add(0, -1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND,
                            0.5F, 0, 0.5F));
                })
                .execute();
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
        BlockPos crystalPos;
        AxisAlignedBB crystalBox;
        Map<EntityPlayer, Double> damages;

        CrystalAttack(BlockPos crystalPos, AxisAlignedBB crystalBox, Map<EntityPlayer, Double> damages) {
            this.crystalPos = crystalPos;
            this.crystalBox = crystalBox;
            this.damages = damages;
        }

        double getTotalDamage() {
            double total = 0;
            for (double damage : damages.values()) total += damage;
            return total;
        }
    }
}
