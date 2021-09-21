package net.sushiclient.client.modules.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.ConfigurationCategory;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.task.forge.TaskExecutor;
import net.sushiclient.client.task.tasks.ItemSlotSwitchTask;
import net.sushiclient.client.utils.EntityInfo;
import net.sushiclient.client.utils.EntityType;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.ReachType;
import net.sushiclient.client.utils.player.DesyncMode;
import net.sushiclient.client.utils.player.DesyncOperator;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.PositionUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class KillAuraModule extends BaseModule {

    private final Configuration<Boolean> player;
    private final Configuration<Boolean> mob;
    private final Configuration<Boolean> passive;
    private final Configuration<Boolean> neutral;
    private final Configuration<Boolean> hostile;
    private final Configuration<ReachType> reach;
    private final Configuration<DoubleRange> range;
    private final Configuration<DoubleRange> wallRange;
    private final Configuration<Boolean> preferAxe;
    private final Configuration<DoubleRange> enemyHp;
    private final Configuration<DoubleRange> minSelfHp;
    private final Configuration<DoubleRange> selfPingMultiplier;
    private final Configuration<Boolean> useInputs;
    private final Configuration<Boolean> constantSpeed;
    private DesyncOperator operator;

    public KillAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);

        ConfigurationCategory targetCategory = provider.getCategory("target", "Target Settings", null);
        player = targetCategory.get("player", "Player", null, Boolean.class, true);
        mob = targetCategory.get("mob", "Mob", null, Boolean.class, true);
        passive = targetCategory.get("passive", "Passive", null, Boolean.class, true);
        neutral = targetCategory.get("neutral", "Neutral", null, Boolean.class, true);
        hostile = targetCategory.get("hostile", "Hostile", null, Boolean.class, true);

        ConfigurationCategory rangeCategory = provider.getCategory("range", "Range Settings", null);
        reach = rangeCategory.get("reach", "Reach", null, ReachType.class, ReachType.LEGIT);
        range = rangeCategory.get("range", "Range", null, DoubleRange.class, new DoubleRange(4, 10, 1, 0.1, 1));
        wallRange = rangeCategory.get("wall_range", "Wall Range", null, DoubleRange.class, new DoubleRange(3, 6, 1, 0.1, 1));

        ConfigurationCategory damage = provider.getCategory("damage", "Damage Settings", null);
        preferAxe = damage.get("prefer_axe", "Prefer Axe", null, Boolean.class, true);
        enemyHp = damage.get("enemy_hp", "Enemy HP", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.1, 1));
        minSelfHp = damage.get("min_self_hp", "Min Self HP", null, DoubleRange.class, new DoubleRange(6, 20, 0, 0.1, 1));

        ConfigurationCategory predict = provider.getCategory("predict", "Predict Settings", null);
        selfPingMultiplier = predict.get("self_ping_multiplier", "Self Multiplier", null, DoubleRange.class, new DoubleRange(1, 10, 0, 0.1, 1));
        useInputs = predict.get("use_inputs", "Use Inputs", null, Boolean.class, true);
        constantSpeed = predict.get("constant_speed", "Constant Speed", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        PositionUtils.close(operator);
        operator = null;
    }


    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        if (getPlayer().getHealth() < minSelfHp.getValue().getCurrent()) {
            PositionUtils.close(operator);
            operator = null;
            return;
        }
        ArrayList<EntityLivingBase> players = new ArrayList<>();
        EntityLivingBase dying = null;
        for (EntityInfo<EntityLivingBase> info : EntityUtils.getNearbyEntities(getPlayer().getPositionVector(), EntityLivingBase.class)) {
            if (info.getDistanceSq() > 40) break;
            EntityLivingBase entity = info.getEntity();
            if (!EntityType.match(entity, player.getValue(), false, mob.getValue(), passive.getValue(), neutral.getValue(), hostile.getValue()))
                continue;
            Vec3d selfOffset = EntityUtils.getPingOffset(getPlayer(), useInputs.getValue(), constantSpeed.getValue(), selfPingMultiplier.getValue().getCurrent());
            Vec3d self = getPlayer().getPositionVector().add(selfOffset);
            Vec3d predicted;
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                Vec3d offset = EntityUtils.getPingOffset(player, useInputs.getValue(), constantSpeed.getValue(), selfPingMultiplier.getValue().getCurrent());
                predicted = player.getPositionVector().add(offset);
            } else {
                predicted = entity.getPositionVector();
            }
            if (EntityUtils.canInteract(self.add(0, 1.62, 0), predicted.add(0, 1.62, 0),
                    range.getValue().getCurrent(), wallRange.getValue().getCurrent(), reach.getValue())) {
                players.add(entity);
                if (entity.getHealth() < enemyHp.getValue().getCurrent()) {
                    dying = entity;
                    break;
                }
            }
        }
        EntityLivingBase target;
        if (dying != null) {
            target = dying;
        } else if (!players.isEmpty()) {
            players.sort(Comparator.comparing(it -> getPlayer().getPositionVector().squareDistanceTo(it.getPositionVector())));
            target = players.get(0);
        } else {
            PositionUtils.close(operator);
            operator = null;
            return;
        }
        TaskExecutor.newTaskChain()
                .supply(() -> InventoryUtils.findBestWeapon(true, preferAxe.getValue()))
                .then(new ItemSlotSwitchTask())
                .execute();
        if (operator == null) operator = PositionUtils.desync();
        operator.desyncMode(DesyncMode.LOOK);
        operator.lookAt(target.getPositionVector().add(0, target.getEyeHeight(), 0));
        if (getPlayer().getCooledAttackStrength(0) > 0.9) {
            PositionUtils.on(() -> {
                getController().attackEntity(getPlayer(), target);
                getPlayer().swingArm(EnumHand.MAIN_HAND);
            });
        }
    }

    @Override
    public String getDefaultName() {
        return "KillAura";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.COMBAT;
    }
}
