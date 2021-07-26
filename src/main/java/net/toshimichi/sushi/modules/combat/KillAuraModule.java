package net.toshimichi.sushi.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.toshimichi.sushi.config.Configuration;
import net.toshimichi.sushi.config.ConfigurationCategory;
import net.toshimichi.sushi.config.RootConfigurations;
import net.toshimichi.sushi.config.data.DoubleRange;
import net.toshimichi.sushi.events.EventHandler;
import net.toshimichi.sushi.events.EventHandlers;
import net.toshimichi.sushi.events.EventTiming;
import net.toshimichi.sushi.events.tick.ClientTickEvent;
import net.toshimichi.sushi.modules.*;
import net.toshimichi.sushi.task.forge.TaskExecutor;
import net.toshimichi.sushi.task.tasks.ItemSlotSwitchTask;
import net.toshimichi.sushi.utils.EntityUtils;
import net.toshimichi.sushi.utils.player.DesyncCloseable;
import net.toshimichi.sushi.utils.player.DesyncMode;
import net.toshimichi.sushi.utils.player.InventoryUtils;
import net.toshimichi.sushi.utils.player.PositionUtils;

import java.util.ArrayList;
import java.util.Comparator;

public class KillAuraModule extends BaseModule {

    private final Configuration<DoubleRange> range;
    private final Configuration<DoubleRange> wallRange;
    private final Configuration<Boolean> preferAxe;
    private final Configuration<DoubleRange> enemyHp;
    private final Configuration<DoubleRange> minSelfHp;
    private final Configuration<DoubleRange> selfPingMultiplier;
    private final Configuration<Boolean> useInputs;
    private final Configuration<Boolean> constantSpeed;

    public KillAuraModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        ConfigurationCategory rangeCategory = provider.getCategory("range", "Range Settings", null);
        range = rangeCategory.get("range", "Range", null, DoubleRange.class, new DoubleRange(6, 10, 1, 0.1, 1));
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
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        if (getPlayer().getHealth() < minSelfHp.getValue().getCurrent()) return;
        if (getPlayer().getCooledAttackStrength(0) < 1) return;
        ArrayList<EntityPlayer> players = new ArrayList<>();
        EntityPlayer dying = null;
        for (EntityPlayer player : EntityUtils.getNearbyPlayers(20)) {
            Vec3d selfOffset = EntityUtils.getPingOffset(player, useInputs.getValue(), constantSpeed.getValue(), selfPingMultiplier.getValue().getCurrent());
            Vec3d offset = EntityUtils.getPingOffset(player, useInputs.getValue(), constantSpeed.getValue(), selfPingMultiplier.getValue().getCurrent());
            Vec3d self = getPlayer().getPositionVector().add(selfOffset);
            Vec3d predicted = player.getPositionVector().add(offset);
            if (EntityUtils.canInteract(self.add(0, 1.62, 0), predicted.add(0, 1.62, 0), range.getValue().getCurrent(), wallRange.getValue().getCurrent())) {
                players.add(player);
                if (player.getHealth() < enemyHp.getValue().getCurrent()) {
                    dying = player;
                    break;
                }
            }
        }
        EntityPlayer target;
        if (dying != null) {
            target = dying;
        } else if (!players.isEmpty()) {
            players.sort(Comparator.comparing(it -> getPlayer().getPositionVector().squareDistanceTo(it.getPositionVector())));
            target = players.get(0);
        } else {
            return;
        }
        TaskExecutor.newTaskChain()
                .supply(() -> InventoryUtils.findBestWeapon(true, preferAxe.getValue()))
                .then(new ItemSlotSwitchTask())
                .execute();
        try (DesyncCloseable closeable = PositionUtils.desync(DesyncMode.LOOK)) {
            PositionUtils.lookAt(target.getPositionVector().add(0, target.getEyeHeight(), 0), DesyncMode.LOOK);
            getController().attackEntity(getPlayer(), target);
            getPlayer().swingArm(EnumHand.MAIN_HAND);
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
